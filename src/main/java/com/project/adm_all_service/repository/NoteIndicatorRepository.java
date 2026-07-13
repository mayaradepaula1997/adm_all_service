package com.project.adm_all_service.repository;

import com.project.adm_all_service.model.AppointmentPeriod;
import com.project.adm_all_service.model.Enterprise;
import com.project.adm_all_service.model.NoteIndicator;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.swing.*;
import java.util.List;

public interface NoteIndicatorRepository extends JpaRepository<NoteIndicator, Long> {


    //Verifica se já existe um apontamento para aquela empresa e período
    boolean existsByEnterpriseAndAppointmentPeriod(Enterprise enterprise, AppointmentPeriod appointmentPeriod);

    //Busca todos os apontamentos de um determinado periodo
    List<NoteIndicator> findByAppointmentPeriodId (Long periodId);

    //Busca todos os apontamentos de uma determinada empresa
    List<NoteIndicator> findByEnterpriseId(Long enterpriseId);


}
