package com.project.adm_all_service.repository;

import com.project.adm_all_service.model.Collaborator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import java.util.Optional;

public interface CollaboratorRepository  extends JpaRepository<Collaborator, Long> , JpaSpecificationExecutor<Collaborator> {

    boolean existsByCpf(String cpf);

    Optional<Collaborator> findByCpf(String cpf);
}
