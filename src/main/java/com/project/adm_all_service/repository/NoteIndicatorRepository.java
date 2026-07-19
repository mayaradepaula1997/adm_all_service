package com.project.adm_all_service.repository;

import com.project.adm_all_service.model.AppointmentPeriod;
import com.project.adm_all_service.model.Enterprise;
import com.project.adm_all_service.model.NoteIndicator;
import org.springframework.data.jpa.repository.JpaRepository;


import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface NoteIndicatorRepository extends JpaRepository<NoteIndicator, Long> {

    // Verifica se já existe um apontamento para aquela empresa e período
    boolean existsByEnterpriseAndAppointmentPeriod(
            Enterprise enterprise,
            AppointmentPeriod appointmentPeriod);

    // Busca um apontamento da empresa, período e data
    Optional<NoteIndicator> findByEnterpriseAndAppointmentPeriodAndAppointmentDate(
            Enterprise enterprise,
            AppointmentPeriod appointmentPeriod,
            LocalDate appointmentDate);

    // Busca todos os apontamentos de um determinado período
    List<NoteIndicator> findByAppointmentPeriodId(Long periodId);

    // Busca todos os apontamentos de uma determinada empresa
    List<NoteIndicator> findByEnterpriseId(Long enterpriseId);


}
