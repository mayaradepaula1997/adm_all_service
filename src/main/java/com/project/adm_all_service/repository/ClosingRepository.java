package com.project.adm_all_service.repository;

import com.project.adm_all_service.model.Closing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface ClosingRepository extends JpaRepository<Closing, Long> , JpaSpecificationExecutor<Closing> {

    //Busca o fechamento que tenha o id da empresa e a data informada (mês e ano)
    Optional<Closing> findByEnterprise_IdAndMonthAndYear(Long enterpriseId,Integer month,Integer year);


    //Busca o fechamento pela empresa, mês, ano e pela quinzena
    Optional<Closing> findByEnterprise_IdAndMonthAndYearAndFortnight(Long enterpriseId,Integer month,Integer year,Integer fortnight);



}
