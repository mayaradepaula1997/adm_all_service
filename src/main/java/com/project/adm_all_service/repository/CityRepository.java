package com.project.adm_all_service.repository;

import com.project.adm_all_service.model.City;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface CityRepository extends JpaRepository<City, Long> , JpaSpecificationExecutor<City> {

    //Busca a cidade pelo nome
    Optional<City> findByName(String name);

    Optional<City> findByNameAndUf(String name, String uf);

}
