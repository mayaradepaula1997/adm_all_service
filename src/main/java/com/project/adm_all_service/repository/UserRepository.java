package com.project.adm_all_service.repository;

import com.project.adm_all_service.enums.Role;
import com.project.adm_all_service.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    Optional<User> findByEmail(String email);

    @org.springframework.data.jpa.repository.Query("SELECT u FROM User u JOIN u.roles r WHERE r = :role")
    Page<User> findAdminsByRole(@org.springframework.data.repository.query.Param("role") Role role, Pageable pageable);

    // Lista sub-usuários de um ADMIN_MASTER específico
    Page<User> findByAdminOwner(User adminOwner, Pageable pageable);

    @org.springframework.data.jpa.repository.Query("SELECT COUNT(u) > 0 FROM User u JOIN u.roles r WHERE r = :role")
    boolean existsAdminsByRole(@org.springframework.data.repository.query.Param("role") Role role);
}
