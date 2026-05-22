package com.project.adm_all_service.service;


import com.project.adm_all_service.dtos.request.CityDto;
import com.project.adm_all_service.dtos.request.UpdateCityDto;
import com.project.adm_all_service.dtos.response.CityCreateDto;
import com.project.adm_all_service.exception.BusinessException;
import com.project.adm_all_service.exception.ResourceNotFoundException;
import com.project.adm_all_service.model.City;
import com.project.adm_all_service.repository.CityRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import java.util.Optional;

@Service
public class CityService {

    private final CityRepository cityRepository;

    public CityService(CityRepository cityRepository) {
        this.cityRepository = cityRepository;
    }

    //Criação
    public CityCreateDto cityCreate (CityDto dto){

        Optional<City> cityExists = cityRepository.findByNameAndUf(dto.name(), dto.uf());

        if (cityExists.isPresent()){
            throw new BusinessException("Cidade já existe");

        }
        City city = new City();
        city.setName(dto.name());
        city.setUf(dto.uf());
        city.setCreation(LocalDateTime.now());

        City cituSaved = cityRepository.save(city);

        return new CityCreateDto(
                cituSaved.getId(),
                cituSaved.getName(),
                cituSaved.getUf(),
                cituSaved.getCreation(),
                cituSaved.getEnterprises()

        );

    }

    //Listar todas as cidades
    public Page<CityDto> listCity(int page, int size){

        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());

        return cityRepository.findAll(pageable)

                .map(city -> new CityDto(
                        city.getId(),
                        city.getName(),
                        city.getUf()
                ));
    }

    //Listar uma cidade pelo seu id
    public CityDto cityById (Long id){

        City city = cityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cidade não encontra"));

        return new CityDto(
                city.getId(),
                city.getName(),
                city.getUf()
        );
    }

    //Deletar cidade
    public void deleteCity(Long idDelete){

        City city = cityRepository.findById(idDelete)
                .orElseThrow(() -> new ResourceNotFoundException("Cidade não encontra"));

        cityRepository.delete(city);
    }

    //Atualização da cidade
    public CityDto updateCity (Long id, UpdateCityDto updateCityDto){

        City city = cityRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Cidade não encontrada"));

        if (updateCityDto.name() != null)city.setName(updateCityDto.name());
        if (updateCityDto.uf() != null)city.setUf(updateCityDto.uf());

        City saved = cityRepository.save(city);

        return new CityDto(
                saved.getId(),
                saved.getName(),
                saved.getUf());
    }
}