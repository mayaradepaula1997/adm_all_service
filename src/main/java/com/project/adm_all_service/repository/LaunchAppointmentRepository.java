package com.project.adm_all_service.repository;

import com.project.adm_all_service.model.Collaborator;
import com.project.adm_all_service.model.LaunchAppointment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface LaunchAppointmentRepository extends JpaRepository<LaunchAppointment, Long> {

    //Busca todos os lançamentos que um determinado apontamento
    List<LaunchAppointment> findByNoteIndicatorId(Long noteIndicatorId);

    //Busca todos os lançametos de um determinado colaborador
    List<LaunchAppointment> findByCollaboratorId(Long collaboratorId);

    //Verifica se existe uma lançamento com aquele determinado apontamento e aquele determinando colaborador
    //Não deixar cadastrar duas vezes o mesmo colaborador em um lançamento
    boolean existsByNoteIndicatorIdAndCollaboratorId(Long noteIndicatorId, Long collaboratorId);

    boolean existsByCollaboratorAndNoteIndicatorAppointmentDate(Collaborator collaborator, LocalDate appointmentDate);

    boolean existsByCollaboratorAndNoteIndicatorAppointmentDateAndNoteIndicatorEnterprise(Collaborator collaborator, LocalDate appointmentDate, com.project.adm_all_service.model.Enterprise enterprise);

    boolean existsByCollaboratorAndNoteIndicatorAppointmentDateAndStatusLaunch(Collaborator collaborator, LocalDate appointmentDate, com.project.adm_all_service.enums.StatusLaunch statusLaunch);

    boolean existsByCollaboratorAndNoteIndicatorAppointmentDateAndStatusLaunchAndIdNot(Collaborator collaborator, LocalDate appointmentDate, com.project.adm_all_service.enums.StatusLaunch statusLaunch, Long id);

    /**
     * Busca lançamentos pelo colaborador e data — retorna lista pois o colaborador
     * pode ter lançamentos em múltiplas empresas na mesma data.
     */
    List<LaunchAppointment> findByCollaborator_IdAndNoteIndicator_AppointmentDate(
            Long collaboratorId, LocalDate appointmentDate);

    /**
     * Busca lançamento pelo colaborador, data e empresa — usado para salvar o valor
     * da diária pelo gestor de forma precisa por empresa.
     */
    Optional<LaunchAppointment> findByCollaborator_IdAndNoteIndicator_AppointmentDateAndNoteIndicator_Enterprise_Id(
            Long collaboratorId, LocalDate appointmentDate, Long enterpriseId);
}

