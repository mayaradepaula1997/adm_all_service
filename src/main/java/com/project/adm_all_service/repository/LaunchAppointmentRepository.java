package com.project.adm_all_service.repository;

import com.project.adm_all_service.model.Collaborator;
import com.project.adm_all_service.model.LaunchAppointment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface LaunchAppointmentRepository extends JpaRepository<LaunchAppointment, Long> {

    //Busca todos os lançamentos que um determinado apontamento
    List<LaunchAppointment> findByNoteIndicatorId(Long noteIndicatorId);

    //Busca todos os lançametos de um determinado colaborador
    List<LaunchAppointment> findByCollaboratorId(Long collaboratorId);

    //Verifica se existe uma lançamento com aquele determinado apontamento e aquele determinando colaborador
    //Não deixar cadastrar duas vezes o mesmo colaborador em um lançamento
    boolean existsByNoteIndicatorIdAndCollaboratorId(Long noteIndicatorId,Long collaboratorId);

    boolean existsByCollaboratorAndNoteIndicatorAppointmentDate(Collaborator collaborator,LocalDate appointmentDate);
}
