package com.project.adm_all_service.service;

import com.project.adm_all_service.dtos.request.GestorSaveDailyValuesRequestDto;
import com.project.adm_all_service.dtos.response.ClosingResponseDto;
import com.project.adm_all_service.enums.StatusLaunch;
import com.project.adm_all_service.exception.ResourceNotFoundException;
import com.project.adm_all_service.model.*;
import com.project.adm_all_service.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ClosingService {

    private final NoteIndicatorRepository noteIndicatorRepository;
    private final CityRepository cityRepository;
    private final EnterpriseRepository enterpriseRepository;
    private final ClosingRepository closingRepository;
    private final LaunchAppointmentRepository launchAppointmentRepository;

    public ClosingService(NoteIndicatorRepository noteIndicatorRepository,
                          CityRepository cityRepository,
                          EnterpriseRepository enterpriseRepository,
                          ClosingRepository closingRepository,
                          LaunchAppointmentRepository launchAppointmentRepository) {
        this.noteIndicatorRepository = noteIndicatorRepository;
        this.cityRepository = cityRepository;
        this.enterpriseRepository = enterpriseRepository;
        this.closingRepository = closingRepository;
        this.launchAppointmentRepository = launchAppointmentRepository;
    }

    /**
     * Salva os valores de diária por dia por colaborador definidos pelo gestor.
     * Atualiza o campo dailyValue em cada LaunchAppointment correspondente.
     */
    @Transactional
    public void saveDailyValues(GestorSaveDailyValuesRequestDto request) {
        for (var item : request.dailyValues()) {
            LocalDate date = LocalDate.parse(item.date());
            launchAppointmentRepository
                    .findByCollaborator_IdAndNoteIndicator_AppointmentDate(item.collaboratorId(), date)
                    .ifPresent(la -> {
                        la.setDailyValue(item.dailyValue());
                        launchAppointmentRepository.save(la);
                    });
        }
    }

    /**
     * Gera o relatório de fechamento da quinzena para uma empresa e cidade.
     * Horas extras = 10% do valor da diária.
     */
    public ClosingResponseDto generateClosing(Long cityId, Long enterpriseId,
                                               Integer month, Integer year,
                                               Integer fortnight, BigDecimal dailyValue) {

        City city = cityRepository.findById(cityId)
                .orElseThrow(() -> new ResourceNotFoundException("Cidade não encontrada"));

        Enterprise enterprise = enterpriseRepository.findById(enterpriseId)
                .orElseThrow(() -> new ResourceNotFoundException("Empresa não encontrada"));

        // Define o intervalo de datas da quinzena
        LocalDate start;
        LocalDate end;
        if (fortnight == 1) {
            start = LocalDate.of(year, month, 1);
            end = LocalDate.of(year, month, 15);
        } else {
            start = LocalDate.of(year, month, 16);
            end = YearMonth.of(year, month).atEndOfMonth();
        }

        // Busca todos os NoteIndicators do período para a empresa e cidade
        List<NoteIndicator> noteIndicators = noteIndicatorRepository
                .findByEnterprise_IdAndCity_IdAndAppointmentDateBetween(enterpriseId, cityId, start, end);

        // Verifica se existe fechamento registrado
        Optional<Closing> existingClosing = closingRepository
                .findByEnterprise_IdAndMonthAndYearAndFortnight(enterpriseId, month, year, fortnight);

        boolean isClosed = existingClosing.isPresent() &&
                existingClosing.get().getClosingStatus() != null &&
                existingClosing.get().getClosingStatus().name().equals("FECHADO");

        String closedAt = existingClosing
                .map(c -> c.getClosedIn() != null ? c.getClosedIn().format(DateTimeFormatter.ofPattern("dd/MM/yyyy, HH:mm")) : null)
                .orElse(null);

        // Agrupa os lançamentos por colaborador
        Map<Long, List<LaunchAppointment>> launchByCollaborator = new LinkedHashMap<>();
        for (NoteIndicator ni : noteIndicators) {
            for (LaunchAppointment la : ni.getLaunchAppointments()) {
                Long collabId = la.getCollaborator().getId();
                launchByCollaborator.computeIfAbsent(collabId, k -> new ArrayList<>()).add(la);
            }
        }

        BigDecimal totalDiarias = BigDecimal.ZERO;
        BigDecimal totalOvertimeValue = BigDecimal.ZERO;

        List<ClosingResponseDto.CollaboratorClosingDto> collaboratorDtos = new ArrayList<>();

        for (Map.Entry<Long, List<LaunchAppointment>> entry : launchByCollaborator.entrySet()) {
            Collaborator collaborator = entry.getValue().get(0).getCollaborator();
            List<LaunchAppointment> launches = entry.getValue();

            // Dias com presença
            long presenceDays = launches.stream()
                    .filter(la -> la.getStatusLaunch() == StatusLaunch.PRESENCE)
                    .count();

            // Total de horas extras em decimal
            BigDecimal overtimeHoursTotal = launches.stream()
                    .filter(la -> la.getOvertime() != null)
                    .map(LaunchAppointment::getOvertime)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal collabDailyValue = dailyValue != null ? dailyValue : BigDecimal.ZERO;

            // Soma o valor de diária salvo por dia; se não salvo ainda, usa o valor global como fallback
            BigDecimal totalDailyValue = launches.stream()
                    .filter(la -> la.getStatusLaunch() == StatusLaunch.PRESENCE)
                    .map(la -> la.getDailyValue() != null ? la.getDailyValue() : collabDailyValue)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            // Descobre a base da diária para horas extras (primeira diária salva, ou fallback)
            BigDecimal baseDailyForOvertime = launches.stream()
                    .filter(la -> la.getStatusLaunch() == StatusLaunch.PRESENCE && la.getDailyValue() != null && la.getDailyValue().compareTo(BigDecimal.ZERO) > 0)
                    .map(LaunchAppointment::getDailyValue)
                    .findFirst()
                    .orElse(collabDailyValue);

            // Horas extras = 10% do valor da diária por hora extra
            BigDecimal overtimeRate = baseDailyForOvertime.multiply(new BigDecimal("0.10"));
            BigDecimal overtimeValue = overtimeRate.multiply(overtimeHoursTotal).setScale(2, RoundingMode.HALF_UP);

            BigDecimal total = totalDailyValue.add(overtimeValue);

            totalDiarias = totalDiarias.add(totalDailyValue);
            totalOvertimeValue = totalOvertimeValue.add(overtimeValue);

            // Monta os dias
            List<ClosingResponseDto.DayEntryDto> days = new ArrayList<>();
            for (LocalDate d = start; !d.isAfter(end); d = d.plusDays(1)) {
                final LocalDate date = d;
                Optional<LaunchAppointment> launch = launches.stream()
                        .filter(la -> {
                            NoteIndicator ni = la.getNoteIndicator();
                            return ni != null && date.equals(ni.getAppointmentDate());
                        })
                        .findFirst();

                if (launch.isPresent()) {
                    LaunchAppointment la = launch.get();
                    days.add(new ClosingResponseDto.DayEntryDto(
                            date,
                            la.getStatusLaunch() != null ? la.getStatusLaunch().name() : null,
                            la.getOvertime(),
                            la.getObservation(),
                            la.getDailyValue()
                    ));
                } else {
                    days.add(new ClosingResponseDto.DayEntryDto(date, null, null, null, null));
                }
            }

            collaboratorDtos.add(new ClosingResponseDto.CollaboratorClosingDto(
                    collaborator.getId(),
                    collaborator.getName(),
                    collaborator.getCpf(),
                    collaborator.getPix(),
                    days,
                    (int) presenceDays,
                    collabDailyValue,
                    totalDailyValue,
                    overtimeHoursTotal,
                    overtimeValue,
                    total
            ));
        }

        BigDecimal grandTotal = totalDiarias.add(totalOvertimeValue);

        return new ClosingResponseDto(
                cityId,
                city.getName(),
                enterpriseId,
                enterprise.getName(),
                month,
                year,
                fortnight,
                start,
                end,
                dailyValue,
                isClosed,
                closedAt,
                collaboratorDtos,
                totalDiarias,
                totalOvertimeValue,
                grandTotal
        );
    }

    /**
     * Resumo da quinzena atual para um colaborador específico (para exibição na listagem de RH).
     */
    public Map<String, Object> getCollaboratorFortnightSummary(Long collaboratorId) {
        LocalDate today = LocalDate.now();
        LocalDate start;
        LocalDate end;
        int fortnight;

        if (today.getDayOfMonth() <= 15) {
            start = LocalDate.of(today.getYear(), today.getMonth(), 1);
            end = LocalDate.of(today.getYear(), today.getMonth(), 15);
            fortnight = 1;
        } else {
            start = LocalDate.of(today.getYear(), today.getMonth(), 16);
            end = YearMonth.from(today).atEndOfMonth();
            fortnight = 2;
        }

        // Busca todos os lançamentos do colaborador no período
        List<NoteIndicator> noteIndicators = noteIndicatorRepository
                .findByEnterprise_IdAndAppointmentDateBetween(0L, start, end); // placeholder

        // Query específica por colaborador via LaunchAppointment
        long presenceDays = 0;
        BigDecimal overtimeTotal = BigDecimal.ZERO;

        // Buscamos via todos os NoteIndicators e filtramos por collaboratorId
        List<NoteIndicator> all = noteIndicatorRepository
                .findByEnterprise_IdAndAppointmentDateBetween(null, start, end);

        // Usamos query mais simples - busca todos do período e filtra
        List<NoteIndicator> allPeriod = noteIndicatorRepository.findAll().stream()
                .filter(ni -> !ni.getAppointmentDate().isBefore(start) && !ni.getAppointmentDate().isAfter(end))
                .collect(Collectors.toList());

        for (NoteIndicator ni : allPeriod) {
            for (LaunchAppointment la : ni.getLaunchAppointments()) {
                if (la.getCollaborator() != null && la.getCollaborator().getId().equals(collaboratorId)) {
                    if (la.getStatusLaunch() == StatusLaunch.PRESENCE) {
                        presenceDays++;
                    }
                    if (la.getOvertime() != null) {
                        overtimeTotal = overtimeTotal.add(la.getOvertime());
                    }
                }
            }
        }

        Map<String, Object> summary = new LinkedHashMap<>();
        summary.put("collaboratorId", collaboratorId);
        summary.put("fortnight", fortnight);
        summary.put("periodStart", start.toString());
        summary.put("periodEnd", end.toString());
        summary.put("totalDays", presenceDays);
        summary.put("overtimeHours", overtimeTotal);

        return summary;
    }
}
