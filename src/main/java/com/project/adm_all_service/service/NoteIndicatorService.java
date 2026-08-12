package com.project.adm_all_service.service;

import com.project.adm_all_service.dtos.request.LaunchAppointmentRequestDto;
import com.project.adm_all_service.dtos.request.LaunchAppointmentUpdateDto;
import com.project.adm_all_service.dtos.request.NoteIndicatorRequestDto;
import com.project.adm_all_service.dtos.request.NoteIndicatorUpdateDto;
import com.project.adm_all_service.dtos.response.NoteIndicatorResponseDto;
import com.project.adm_all_service.enums.Fortnight;
import com.project.adm_all_service.exception.BusinessException;
import com.project.adm_all_service.exception.ResourceNotFoundException;
import com.project.adm_all_service.mapper.LaunchAppointmentMapper;
import com.project.adm_all_service.mapper.NoteIndicatorMapper;
import com.project.adm_all_service.model.*;
import com.project.adm_all_service.repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class NoteIndicatorService {

    private final AppointmentPeriodService appointmentPeriodService;
    private final NoteIndicatorRepository noteIndicatorRepository;
    private final CityRepository cityRepository;
    private final EnterpriseRepository enterpriseRepository;
    private final CollaboratorRepository collaboratorRepository;
    private final LaunchAppointmentRepository launchAppointmentRepository;


    public NoteIndicatorService(AppointmentPeriodService appointmentPeriodService, NoteIndicatorRepository noteIndicatorRepository, CityRepository cityRepository, EnterpriseRepository enterpriseRepository, CollaboratorRepository collaboratorRepository, LaunchAppointmentRepository launchAppointmentRepository) {
        this.appointmentPeriodService = appointmentPeriodService;
        this.noteIndicatorRepository = noteIndicatorRepository;
        this.cityRepository = cityRepository;
        this.enterpriseRepository = enterpriseRepository;
        this.collaboratorRepository = collaboratorRepository;
        this.launchAppointmentRepository = launchAppointmentRepository;
    }

    @Transactional
    public NoteIndicatorResponseDto create(NoteIndicatorRequestDto noteIndicatorRequestDto, User user){

        //Buscar a cidade
        City city = cityRepository.findById(noteIndicatorRequestDto.cityId())
                .orElseThrow(()-> new ResourceNotFoundException("Cidade não encontrada."));

        //Buscar a empresa
        Enterprise enterprise = enterpriseRepository.findById(noteIndicatorRequestDto.enterpriseId())
                .orElseThrow(()-> new ResourceNotFoundException("Empresa não encontrada."));

        validateFortnight(noteIndicatorRequestDto.appointmentDate(), noteIndicatorRequestDto.fortnight());

        validateAppointmentDeadline(noteIndicatorRequestDto.appointmentDate());

        AppointmentPeriod appointmentPeriod =
                appointmentPeriodService.findOrCreate(
                        noteIndicatorRequestDto.appointmentDate(),
                        noteIndicatorRequestDto.fortnight());

       //Instancia a classe utilizando o Mapper
        NoteIndicator noteIndicator = NoteIndicatorMapper.toEntity(city, enterprise,user,appointmentPeriod,noteIndicatorRequestDto.appointmentDate());

        // Evita colaborador repetido na mesma requisição
        Set<Long> collaboratorIds = new HashSet<>();

        for (LaunchAppointmentRequestDto launchDto : noteIndicatorRequestDto.launchAppointments()) {

            if (!collaboratorIds.add(launchDto.collaboratorId())) {
                throw new BusinessException("O colaborador foi informado mais de uma vez.");
            }

            // Pega o id enviado e busca o colaborador no BD
            Collaborator collaborator = collaboratorRepository.findById(launchDto.collaboratorId())
                    .orElseThrow(() -> new ResourceNotFoundException("Colaborador não encontrado."));

            // Verifica se o colaborador já possui lançamento nesta mesma empresa
            if (launchAppointmentRepository
                    .existsByCollaboratorAndNoteIndicatorAppointmentDateAndNoteIndicatorEnterprise(
                            collaborator,
                            noteIndicatorRequestDto.appointmentDate(),
                            enterprise)) {

                throw new BusinessException(
                        "O colaborador já possui um apontamento nesta empresa para esta data.");
            }

            // Verifica se está lançando PRESENÇA e já existe outra PRESENÇA na mesma data em qualquer outra empresa
            if (launchDto.statusLaunch() == com.project.adm_all_service.enums.StatusLaunch.PRESENCE) {
                if (launchAppointmentRepository.existsByCollaboratorAndNoteIndicatorAppointmentDateAndStatusLaunch(
                        collaborator, noteIndicatorRequestDto.appointmentDate(), com.project.adm_all_service.enums.StatusLaunch.PRESENCE)) {
                    throw new BusinessException("O colaborador \"" + collaborator.getName() + "\" já possui um apontamento de presença nesta data em outra empresa.");
                }
            }

            //Instância a classe de lançamento
            LaunchAppointment launchAppointment = LaunchAppointmentMapper.toEntity(launchDto, collaborator);

            // Relaciona o filho ao pai
            launchAppointment.setNoteIndicator(noteIndicator);

            noteIndicator.getLaunchAppointments().add(launchAppointment);

        }
        NoteIndicator saved = noteIndicatorRepository.save(noteIndicator);

        System.out.println("Salvou o NoteIndicator com id: " + saved.getId());

        return NoteIndicatorMapper.toDto(saved);
    }


    //Listar todos por paginação
    public Page<NoteIndicatorResponseDto> findAll(int page, int size){
        Pageable pageable = PageRequest.of(page, size);

        return noteIndicatorRepository.findAll(pageable)
                .map(noteIndicator -> NoteIndicatorMapper.toDto(noteIndicator));

    }
    
    // Filtrar apontamentos por empresa, cidade e datas
    public java.util.List<NoteIndicatorResponseDto> filter(Long enterpriseId, Long cityId, LocalDate start, LocalDate end) {
        return noteIndicatorRepository.findByEnterprise_IdAndCity_IdAndAppointmentDateBetween(enterpriseId, cityId, start, end)
                .stream()
                .map(NoteIndicatorMapper::toDto)
                .collect(Collectors.toList());
    }

    //Buscar por id
    public NoteIndicatorResponseDto findById(Long id) {

        NoteIndicator noteIndicato = noteIndicatorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Apontamento não encontrado."));

        return NoteIndicatorMapper.toDto(noteIndicato);
    }

    //Deletar - Ao excluir um NoteIndicator, todos os seus LaunchAppointment serão excluídos automaticamente.
    public void delete(Long id){

        NoteIndicator noteIndicator = noteIndicatorRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Apontamento não encontrado."));

        noteIndicatorRepository.delete(noteIndicator);
    }

    //Atualizar
    @Transactional
    public NoteIndicatorResponseDto update(
            Long id,
            NoteIndicatorUpdateDto dto) {

        // JOIN FETCH garante que os LaunchAppointments estejam carregados e gerenciados
        // pelo contexto de persistência, permitindo que o dirty-checking do Hibernate
        // detecte as alterações e faça o flush automaticamente ao final da transação.
        NoteIndicator noteIndicator = noteIndicatorRepository.findWithLaunchAppointmentsById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Apontamento não encontrado."));

        // Mapa dos lançamentos existentes indexado pelo ID para lookup O(1)
        Map<Long, LaunchAppointment> launchesById =
                noteIndicator.getLaunchAppointments()
                        .stream()
                        .collect(Collectors.toMap(
                                LaunchAppointment::getId,
                                Function.identity()
                        ));

        for (LaunchAppointmentUpdateDto launchDto : dto.launchAppointments()) {

            if (launchDto.id() != null) {
                // CASO 1: Atualizar lançamento existente
                LaunchAppointment launch = launchesById.get(launchDto.id());

                if (launch == null) {
                    throw new ResourceNotFoundException("Lançamento não encontrado: " + launchDto.id());
                }

                if (launchDto.statusLaunch() == com.project.adm_all_service.enums.StatusLaunch.PRESENCE) {
                    if (launchAppointmentRepository.existsByCollaboratorAndNoteIndicatorAppointmentDateAndStatusLaunchAndIdNot(
                            launch.getCollaborator(), noteIndicator.getAppointmentDate(),
                            com.project.adm_all_service.enums.StatusLaunch.PRESENCE, launch.getId())) {
                        throw new BusinessException("O colaborador \"" + launch.getCollaborator().getName() + "\" já possui um apontamento de presença nesta data em outra empresa.");
                    }
                }

                launch.setStatusLaunch(launchDto.statusLaunch());
                launch.setOvertime(launchDto.overtime());
                launch.setObservation(launchDto.observation());

            } else {
                // CASO 2: Novo lançamento para colaborador adicionado à empresa após a criação do NoteIndicator
                if (launchDto.collaboratorId() == null) {
                    throw new BusinessException("collaboratorId é obrigatório para novos lançamentos.");
                }

                Collaborator collaborator = collaboratorRepository.findById(launchDto.collaboratorId())
                        .orElseThrow(() -> new ResourceNotFoundException("Colaborador não encontrado."));

                // Verifica duplicidade neste NoteIndicator
                boolean jaExiste = noteIndicator.getLaunchAppointments().stream()
                        .anyMatch(la -> la.getCollaborator().getId().equals(launchDto.collaboratorId()));
                if (jaExiste) {
                    throw new BusinessException("O colaborador já possui um lançamento neste apontamento.");
                }

                // Validação de presença duplicada em outra empresa na mesma data
                if (launchDto.statusLaunch() == com.project.adm_all_service.enums.StatusLaunch.PRESENCE) {
                    if (launchAppointmentRepository.existsByCollaboratorAndNoteIndicatorAppointmentDateAndStatusLaunch(
                            collaborator, noteIndicator.getAppointmentDate(),
                            com.project.adm_all_service.enums.StatusLaunch.PRESENCE)) {
                        throw new BusinessException("O colaborador \"" + collaborator.getName() + "\" já possui um apontamento de presença nesta data em outra empresa.");
                    }
                }

                LaunchAppointment newLaunch = new LaunchAppointment();
                newLaunch.setCollaborator(collaborator);
                newLaunch.setStatusLaunch(launchDto.statusLaunch());
                newLaunch.setOvertime(launchDto.overtime());
                newLaunch.setObservation(launchDto.observation());
                newLaunch.setNoteIndicator(noteIndicator);
                noteIndicator.getLaunchAppointments().add(newLaunch);
            }
        }

        noteIndicatorRepository.save(noteIndicator);

        return NoteIndicatorMapper.toDto(noteIndicator);
    }


    //Método que realiza a validação da quinzena
    private void validateFortnight(
            LocalDate appointmentDate,
            Fortnight fortnight) {

        if (appointmentDate.getDayOfMonth() <= 15 &&
                fortnight != Fortnight.FIRST) {

            throw new BusinessException(
                    "A data informada pertence à primeira quinzena.");
        }

        if (appointmentDate.getDayOfMonth() >= 16 &&
                fortnight != Fortnight.SECOND) {

            throw new BusinessException(
                    "A data informada pertence à segunda quinzena.");
        }
    }


   //Método que valida o prazo da quinzena
    // Permite lançar na quinzena atual e na quinzena imediatamente anterior.
    private void validateAppointmentDeadline(LocalDate appointmentDate) {

        LocalDate today = LocalDate.now();

        // Calcula os limites da quinzena atual e da anterior com base na data de hoje
        LocalDate currentFortnightStart;
        LocalDate currentFortnightEnd;
        LocalDate previousFortnightStart;
        LocalDate previousFortnightEnd;

        if (today.getDayOfMonth() <= 15) {
            // Hoje está na 1ª quinzena do mês atual
            currentFortnightStart = LocalDate.of(today.getYear(), today.getMonth(), 1);
            currentFortnightEnd   = LocalDate.of(today.getYear(), today.getMonth(), 15);
            // Quinzena anterior = 2ª quinzena do mês passado
            YearMonth lastMonth = YearMonth.from(today).minusMonths(1);
            previousFortnightStart = LocalDate.of(lastMonth.getYear(), lastMonth.getMonth(), 16);
            previousFortnightEnd   = lastMonth.atEndOfMonth();
        } else {
            // Hoje está na 2ª quinzena do mês atual
            currentFortnightStart = LocalDate.of(today.getYear(), today.getMonth(), 16);
            currentFortnightEnd   = YearMonth.from(today).atEndOfMonth();
            // Quinzena anterior = 1ª quinzena do mês atual
            previousFortnightStart = LocalDate.of(today.getYear(), today.getMonth(), 1);
            previousFortnightEnd   = LocalDate.of(today.getYear(), today.getMonth(), 15);
        }

        boolean isInCurrentFortnight  = !appointmentDate.isBefore(currentFortnightStart)  && !appointmentDate.isAfter(currentFortnightEnd);
        boolean isInPreviousFortnight = !appointmentDate.isBefore(previousFortnightStart) && !appointmentDate.isAfter(previousFortnightEnd);

        if (!isInCurrentFortnight && !isInPreviousFortnight) {
            throw new BusinessException(
                    "O prazo para lançamento desta quinzena foi encerrado.");
        }
    }
}
