package com.project.adm_all_service.repository;

import com.project.adm_all_service.model.AppointmentPeriod;
import com.project.adm_all_service.model.City;
import com.project.adm_all_service.model.Enterprise;
import com.project.adm_all_service.model.NoteIndicator;
import org.springframework.data.jpa.repository.JpaRepository;

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
}
