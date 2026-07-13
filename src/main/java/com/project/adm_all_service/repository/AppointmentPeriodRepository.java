package com.project.adm_all_service.repository;

import com.project.adm_all_service.enums.Fortnight;
import com.project.adm_all_service.model.AppointmentPeriod;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AppointmentPeriodRepository extends JpaRepository<AppointmentPeriod, Long> {

    //Busca um periodo que tenha exatamente ano, mês e quinzena informada - verifica antes de criar
    Optional<AppointmentPeriod> findByYearAndMonthAndFortnight(Integer year, Integer month, Fortnight fortnight);

    /*Verifica se existe ou não um registro com esses dados - recupera o periodo
    boolean existsByYearAndMonthAndFortnight(Integer year, Integer month, Fortnight fortnight);*/
}
