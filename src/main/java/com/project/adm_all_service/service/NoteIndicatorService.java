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

            // Verifica se o colaborador já possui lançamento para esta data
            if (launchAppointmentRepository
                    .existsByCollaboratorAndNoteIndicatorAppointmentDate(
                            collaborator,
                            noteIndicatorRequestDto.appointmentDate())) {

                throw new BusinessException(
                        "O colaborador já possui um apontamento nesta data.");
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

        NoteIndicator noteIndicator = noteIndicatorRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Apontamento não encontrado."));

        Map<Long, LaunchAppointment> launches =
                noteIndicator.getLaunchAppointments()      //Pega a lista do banco
                        .stream()
                        .collect(Collectors.toMap(        //Transforma em um Map/Mapa
                                LaunchAppointment::getId, //A CHAVE do mapa será o ID (Long)
                                Function.identity()       //O VALOR será o próprio objeto completo
                        ));

        for (LaunchAppointmentUpdateDto launchDto : dto.launchAppointments()) {

            LaunchAppointment launch = launches.get(launchDto.id());

            if (launch == null) {
                throw new ResourceNotFoundException(
                        "Lançamento não encontrado.");
            }

            launch.setStatusLaunch(launchDto.statusLaunch());
            launch.setOvertime(launchDto.overtime());
            launch.setObservation(launchDto.observation());
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
    private void validateAppointmentDeadline(
            LocalDate appointmentDate) {

        LocalDate today = LocalDate.now();

        LocalDate deadline;

        if (appointmentDate.getDayOfMonth() <= 15) {

            deadline = LocalDate.of(
                    appointmentDate.getYear(),
                    appointmentDate.getMonth(),
                    15);

        } else {

            YearMonth yearMonth =
                    YearMonth.from(appointmentDate);

            deadline = yearMonth.atEndOfMonth();
        }

        if (today.isAfter(deadline)) {

            throw new BusinessException(
                    "O prazo para lançamento desta quinzena foi encerrado.");
        }
    }
}
