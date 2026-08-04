package com.project.adm_all_service.repository;

import com.project.adm_all_service.model.Collaborator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CollaboratorRepository extends JpaRepository<Collaborator, Long> {

    boolean existsByCpf(String cpf);

    Page<Collaborator> findByEnterprises_Id(Long enterpriseId, Pageable pageable);

    Page<Collaborator> findByCity_Id(Long cityId, Pageable pageable);

    Page<Collaborator> findByEnterprises_IdAndCity_Id(Long enterpriseId, Long cityId, Pageable pageable);
}
