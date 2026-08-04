package com.project.adm_all_service.repository;

import com.project.adm_all_service.model.City;
import com.project.adm_all_service.model.Enterprise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface EnterpriseRepository extends JpaRepository<Enterprise, Long>, JpaSpecificationExecutor<Enterprise> {

    // Buscar empresa pelo documento (CPF ou CNPJ)
    Optional<Enterprise> findByDocumento(String documento);

    boolean existsByCity(City city);
}
