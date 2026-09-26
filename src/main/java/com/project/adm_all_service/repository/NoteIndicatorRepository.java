package com.project.adm_all_service.repository;

import com.project.adm_all_service.model.AppointmentPeriod;
import com.project.adm_all_service.model.Enterprise;
import com.project.adm_all_service.model.NoteIndicator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface NoteIndicatorRepository extends JpaRepository<NoteIndicator, Long> {

    boolean existsByEnterpriseAndAppointmentPeriod(Enterprise enterprise, AppointmentPeriod appointmentPeriod);

    Optional<NoteIndicator> findByEnterpriseAndAppointmentPeriodAndAppointmentDate(
            Enterprise enterprise, AppointmentPeriod appointmentPeriod, LocalDate appointmentDate);

    List<NoteIndicator> findByAppointmentPeriodId(Long periodId);

    List<NoteIndicator> findByEnterpriseId(Long enterpriseId);

    // Busca apontamentos por empresa e cidade em um intervalo de datas (para fechamento)
    List<NoteIndicator> findByEnterprise_IdAndCity_IdAndAppointmentDateBetween(
            Long enterpriseId, Long cityId, LocalDate start, LocalDate end);

    // Busca apontamentos por empresa em um intervalo de datas
    List<NoteIndicator> findByEnterprise_IdAndAppointmentDateBetween(
            Long enterpriseId, LocalDate start, LocalDate end);

    /**
     * Carrega o NoteIndicator junto com seus LaunchAppointments em uma única query (JOIN FETCH).
     * Necessário para garantir que os filhos estejam gerenciados pelo contexto de persistência
     * durante o update, evitando problemas de lazy loading fora de sessão.
     */
    @Query("SELECT ni FROM NoteIndicator ni LEFT JOIN FETCH ni.launchAppointments WHERE ni.id = :id")
    Optional<NoteIndicator> findWithLaunchAppointmentsById(@Param("id") Long id);

    /**
     * Carrega NoteIndicators com LaunchAppointments e Collaborators em uma única query.
     * Elimina o N+1 no generateClosing().
     */
    @Query("SELECT DISTINCT ni FROM NoteIndicator ni " +
           "LEFT JOIN FETCH ni.launchAppointments la " +
           "LEFT JOIN FETCH la.collaborator " +
           "WHERE ni.enterprise.id = :enterpriseId " +
           "AND ni.appointmentDate BETWEEN :start AND :end")
    List<NoteIndicator> findByEnterpriseWithLaunchesAndCollaborators(
            @Param("enterpriseId") Long enterpriseId,
            @Param("start") LocalDate start,
            @Param("end") LocalDate end);
}
