package com.project.adm_all_service.repository;

import com.project.adm_all_service.model.Enterprise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface EnterpriseRepository  extends JpaRepository<Enterprise, Long> , JpaSpecificationExecutor<Enterprise> {

    //Buscar a empresa pelo cnpj
    List<Enterprise>  findByCnpj(String cnpj);
}
