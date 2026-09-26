package com.project.adm_all_service.service;

import com.project.adm_all_service.dtos.response.DashboardEvolutionDto;
import com.project.adm_all_service.dtos.response.DashboardExpenseDto;
import com.project.adm_all_service.dtos.response.DashboardSummaryResponseDto;
import com.project.adm_all_service.enums.StatusLaunch;
import com.project.adm_all_service.model.LaunchAppointment;
import com.project.adm_all_service.repository.LaunchAppointmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class DashboardService {

    private final LaunchAppointmentRepository launchAppointmentRepository;

    public DashboardService(LaunchAppointmentRepository launchAppointmentRepository) {
        this.launchAppointmentRepository = launchAppointmentRepository;
    }

    private BigDecimal calculateExpense(LaunchAppointment la) {
        if (la.getStatusLaunch() != StatusLaunch.PRESENCE) {
            return BigDecimal.ZERO;
        }
        BigDecimal daily = la.getDailyValue() != null ? la.getDailyValue() : BigDecimal.ZERO;
        BigDecimal overtimeHours = la.getOvertime() != null ? la.getOvertime() : BigDecimal.ZERO;
        BigDecimal overtimeValue = daily.multiply(new BigDecimal("0.10")).multiply(overtimeHours).setScale(2, RoundingMode.HALF_UP);
        return daily.add(overtimeValue);
    }

    public DashboardSummaryResponseDto getSummary(Integer month, Integer year, Integer fortnight, Long enterpriseId) {
        LocalDate start;
        LocalDate end;
        LocalDate prevStart;
        LocalDate prevEnd;

        if (fortnight != null) {
            if (fortnight == 1) {
                start = LocalDate.of(year, month, 1);
                end = LocalDate.of(year, month, 15);
                
                int prevMonth = month == 1 ? 12 : month - 1;
                int prevYear = month == 1 ? year - 1 : year;
                prevStart = LocalDate.of(prevYear, prevMonth, 16);
                prevEnd = YearMonth.of(prevYear, prevMonth).atEndOfMonth();
            } else {
                start = LocalDate.of(year, month, 16);
                end = YearMonth.of(year, month).atEndOfMonth();
                
                prevStart = LocalDate.of(year, month, 1);
                prevEnd = LocalDate.of(year, month, 15);
            }
        } else {
            start = LocalDate.of(year, month, 1);
            end = YearMonth.of(year, month).atEndOfMonth();
            
            int prevMonth = month == 1 ? 12 : month - 1;
            int prevYear = month == 1 ? year - 1 : year;
            prevStart = LocalDate.of(prevYear, prevMonth, 1);
            prevEnd = YearMonth.of(prevYear, prevMonth).atEndOfMonth();
        }

        List<LaunchAppointment> launches = launchAppointmentRepository.findAllWithCollaboratorByPeriod(start, end);
        List<LaunchAppointment> prevLaunches = launchAppointmentRepository.findAllWithCollaboratorByPeriod(prevStart, prevEnd);

        Map<Long, DashboardExpenseDto> expensesMap = new HashMap<>();
        BigDecimal globalTotal = BigDecimal.ZERO;
        BigDecimal prevGlobalTotal = BigDecimal.ZERO;

        for (LaunchAppointment la : launches) {
            if (la.getNoteIndicator() == null || la.getNoteIndicator().getEnterprise() == null) continue;
            
            Long entId = la.getNoteIndicator().getEnterprise().getId();
            // Filtro por empresa
            if (enterpriseId != null && !entId.equals(enterpriseId)) continue;

            String entName = la.getNoteIndicator().getEnterprise().getName();
            BigDecimal expense = calculateExpense(la);

            DashboardExpenseDto existing = expensesMap.getOrDefault(entId, new DashboardExpenseDto(entId, entName, BigDecimal.ZERO));
            expensesMap.put(entId, new DashboardExpenseDto(entId, entName, existing.totalExpense().add(expense)));
            globalTotal = globalTotal.add(expense);
        }

        for (LaunchAppointment la : prevLaunches) {
            if (la.getNoteIndicator() == null || la.getNoteIndicator().getEnterprise() == null) continue;
            Long entId = la.getNoteIndicator().getEnterprise().getId();
            if (enterpriseId != null && !entId.equals(enterpriseId)) continue;
            prevGlobalTotal = prevGlobalTotal.add(calculateExpense(la));
        }

        BigDecimal growthPercentage = BigDecimal.ZERO;
        if (prevGlobalTotal.compareTo(BigDecimal.ZERO) > 0) {
            growthPercentage = globalTotal.subtract(prevGlobalTotal)
                .divide(prevGlobalTotal, 4, RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100"));
        } else if (globalTotal.compareTo(BigDecimal.ZERO) > 0) {
            growthPercentage = new BigDecimal("100.00");
        }

        List<DashboardExpenseDto> sortedExpenses = expensesMap.values().stream()
                .sorted((a, b) -> b.totalExpense().compareTo(a.totalExpense()))
                .collect(Collectors.toList());

        return new DashboardSummaryResponseDto(sortedExpenses, globalTotal, prevGlobalTotal, growthPercentage);
    }

    public List<com.project.adm_all_service.dtos.response.DashboardCollaboratorExpenseDto> getDetails(Integer month, Integer year, Integer fortnight, Long enterpriseId) {
        LocalDate start;
        LocalDate end;
        if (fortnight != null) {
            if (fortnight == 1) {
                start = LocalDate.of(year, month, 1);
                end = LocalDate.of(year, month, 15);
            } else {
                start = LocalDate.of(year, month, 16);
                end = YearMonth.of(year, month).atEndOfMonth();
            }
        } else {
            start = LocalDate.of(year, month, 1);
            end = YearMonth.of(year, month).atEndOfMonth();
        }

        List<LaunchAppointment> launches = launchAppointmentRepository.findAllWithCollaboratorByPeriod(start, end);

        Map<Long, com.project.adm_all_service.dtos.response.DashboardCollaboratorExpenseDto> map = new HashMap<>();

        for (LaunchAppointment la : launches) {
            if (la.getNoteIndicator() == null || la.getNoteIndicator().getEnterprise() == null) continue;
            if (enterpriseId != null && !la.getNoteIndicator().getEnterprise().getId().equals(enterpriseId)) continue;
            if (la.getCollaborator() == null) continue;

            Long collabId = la.getCollaborator().getId();
            BigDecimal expense = calculateExpense(la);
            int presence = la.getStatusLaunch() == StatusLaunch.PRESENCE ? 1 : 0;
            BigDecimal overtime = la.getOvertime() != null ? la.getOvertime() : BigDecimal.ZERO;

            if (map.containsKey(collabId)) {
                com.project.adm_all_service.dtos.response.DashboardCollaboratorExpenseDto existing = map.get(collabId);
                map.put(collabId, new com.project.adm_all_service.dtos.response.DashboardCollaboratorExpenseDto(
                    collabId,
                    existing.collaboratorName(),
                    existing.cpf(),
                    existing.presenceDays() + presence,
                    existing.overtimeHours().add(overtime),
                    existing.totalExpense().add(expense)
                ));
            } else {
                map.put(collabId, new com.project.adm_all_service.dtos.response.DashboardCollaboratorExpenseDto(
                    collabId,
                    la.getCollaborator().getName(),
                    la.getCollaborator().getCpf(),
                    presence,
                    overtime,
                    expense
                ));
            }
        }

        return map.values().stream()
                .sorted((a, b) -> b.totalExpense().compareTo(a.totalExpense()))
                .collect(Collectors.toList());
    }

    public List<DashboardEvolutionDto> getEvolution(Integer year, Long enterpriseId) {
        LocalDate start = LocalDate.of(year, 1, 1);
        LocalDate end = LocalDate.of(year, 12, 31);
        List<LaunchAppointment> launches = launchAppointmentRepository.findAllWithCollaboratorByPeriod(start, end);

        // Agrupa por "Mês - Quinzena"
        Map<String, List<LaunchAppointment>> launchesByPeriod = new TreeMap<>();
        for (LaunchAppointment la : launches) {
            if (la.getNoteIndicator() == null || la.getNoteIndicator().getAppointmentDate() == null) continue;
            if (la.getNoteIndicator().getEnterprise() == null) continue;
            
            // Filtro por empresa
            if (enterpriseId != null && !la.getNoteIndicator().getEnterprise().getId().equals(enterpriseId)) continue;

            LocalDate date = la.getNoteIndicator().getAppointmentDate();
            int m = date.getMonthValue();
            int f = date.getDayOfMonth() <= 15 ? 1 : 2;
            String periodKey = String.format("%02d - %dª Quinzena", m, f);
            launchesByPeriod.computeIfAbsent(periodKey, k -> new ArrayList<>()).add(la);
        }

        List<DashboardEvolutionDto> evolution = new ArrayList<>();
        for (Map.Entry<String, List<LaunchAppointment>> entry : launchesByPeriod.entrySet()) {
            String periodName = entry.getKey();
            List<LaunchAppointment> periodLaunches = entry.getValue();

            int m = periodLaunches.get(0).getNoteIndicator().getAppointmentDate().getMonthValue();
            int f = periodLaunches.get(0).getNoteIndicator().getAppointmentDate().getDayOfMonth() <= 15 ? 1 : 2;

            Map<Long, DashboardExpenseDto> expensesMap = new HashMap<>();
            for (LaunchAppointment la : periodLaunches) {
                Long entId = la.getNoteIndicator().getEnterprise().getId();
                String entName = la.getNoteIndicator().getEnterprise().getName();
                BigDecimal expense = calculateExpense(la);

                DashboardExpenseDto existing = expensesMap.getOrDefault(entId, new DashboardExpenseDto(entId, entName, BigDecimal.ZERO));
                expensesMap.put(entId, new DashboardExpenseDto(entId, entName, existing.totalExpense().add(expense)));
            }

            List<DashboardExpenseDto> expensesList = new ArrayList<>(expensesMap.values());
            evolution.add(new DashboardEvolutionDto(periodName, year, m, f, expensesList));
        }

        return evolution;
    }
}
