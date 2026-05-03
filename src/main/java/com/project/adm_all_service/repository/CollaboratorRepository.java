package com.project.adm_all_service.repository;

import com.project.adm_all_service.model.Collaborator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface CollaboratorRepository  extends JpaRepository<Collaborator, Long> , JpaSpecificationExecutor<Collaborator> {

    List<Collaborator> findByCpf(String cpf);
}
