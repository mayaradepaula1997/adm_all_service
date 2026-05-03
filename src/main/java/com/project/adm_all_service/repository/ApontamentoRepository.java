package com.project.adm_all_service.repository;

import com.project.adm_all_service.model.Apontamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ApontamentoRepository extends JpaRepository<Apontamento, Long>, JpaSpecificationExecutor<Apontamento> {
}
