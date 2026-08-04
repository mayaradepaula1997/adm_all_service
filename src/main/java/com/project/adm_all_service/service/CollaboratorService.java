package com.project.adm_all_service.service;


import com.project.adm_all_service.dtos.request.CollaboratorRequestDTO;
import com.project.adm_all_service.dtos.request.CollaboratorUpdateDTO;
import com.project.adm_all_service.dtos.response.CollaboratorResponseDTO;
import com.project.adm_all_service.exception.BusinessException;
import com.project.adm_all_service.exception.ResourceNotFoundException;
import com.project.adm_all_service.mapper.CollaboratorMapper;
import com.project.adm_all_service.model.City;
import com.project.adm_all_service.model.Collaborator;
import com.project.adm_all_service.model.Enterprise;
import com.project.adm_all_service.repository.CityRepository;
import com.project.adm_all_service.repository.CollaboratorRepository;
import com.project.adm_all_service.repository.EnterpriseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;
import java.util.HashSet;
import java.util.Set;

@Service
public class CollaboratorService {

    private final CollaboratorRepository collaboratorRepository;
    private final CityRepository cityRepository;
    private final EnterpriseRepository enterpriseRepository;
    private final CollaboratorMapper collaboratorMapper;

    public CollaboratorService(CollaboratorRepository collaboratorRepository,
                               CityRepository cityRepository,
                               EnterpriseRepository enterpriseRepository,
                               CollaboratorMapper collaboratorMapper) {
        this.collaboratorRepository = collaboratorRepository;
        this.cityRepository = cityRepository;
        this.enterpriseRepository = enterpriseRepository;
        this.collaboratorMapper = collaboratorMapper;
    }

    // VALIDAÇÃO DA IDADE
    private void validateAge(LocalDate birthDate) {
        int age = Period.between(birthDate, LocalDate.now()).getYears();
        if (age < 18) {
            throw new BusinessException("O colaborador deve possuir pelo menos 18 anos");
        }
    }

    // VALIDAÇÃO DO CPF ÚNICO
    private void validateCpf(String cpf) {
        if (collaboratorRepository.existsByCpf(cpf)) {
            throw new BusinessException("Já existe um colaborador com esse CPF");
        }
    }

    @Transactional
    public CollaboratorResponseDTO create(CollaboratorRequestDTO dto) {

        validateCpf(dto.cpf());
        validateAge(dto.dateOfBirth());

        // Verificar se a cidade existe
        City city = cityRepository.findById(dto.cityId())
                .orElseThrow(() -> new ResourceNotFoundException("Cidade não encontrada"));

        // Buscar e validar todas as empresas informadas
        Set<Enterprise> enterprises = new HashSet<>();
        for (Long enterpriseId : dto.enterpriseIds()) {
            Enterprise enterprise = enterpriseRepository.findById(enterpriseId)
                    .orElseThrow(() -> new ResourceNotFoundException("Empresa não encontrada: " + enterpriseId));
            enterprises.add(enterprise);
        }

        Collaborator collaborator = new Collaborator();
        collaborator.setName(dto.name());
        collaborator.setCpf(dto.cpf());
        collaborator.setRg(dto.rg());
        collaborator.setDate_of_birth(dto.dateOfBirth());
        collaborator.setAddress1(dto.address1());
        collaborator.setAddress2(dto.address2());
        collaborator.setPix(dto.pix());
        collaborator.setFatherName(dto.fatherName());
        collaborator.setFatherCpf(dto.fatherCpf());
        collaborator.setMotherName(dto.motherName());
        collaborator.setMotherCpf(dto.motherCpf());
        collaborator.setTransportMode(dto.transportMode());
        collaborator.setEnterprises(enterprises);
        collaborator.setCity(city);

        Collaborator saved = collaboratorRepository.save(collaborator);
        return collaboratorMapper.toResponse(saved);
    }

    // Listar todos os colaboradores por paginação
    public Page<CollaboratorResponseDTO> listAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
        return collaboratorRepository.findAll(pageable).map(collaboratorMapper::toResponse);
    }

    // Listar colaboradores por empresa
    public Page<CollaboratorResponseDTO> listByEnterprise(Long enterpriseId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
        return collaboratorRepository.findByEnterprises_Id(enterpriseId, pageable)
                .map(collaboratorMapper::toResponse);
    }

    // Listar colaboradores por cidade
    public Page<CollaboratorResponseDTO> listByCity(Long cityId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
        return collaboratorRepository.findByCity_Id(cityId, pageable)
                .map(collaboratorMapper::toResponse);
    }

    // Listar colaboradores por empresa e cidade
    public Page<CollaboratorResponseDTO> listByEnterpriseAndCity(Long enterpriseId, Long cityId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
        return collaboratorRepository.findByEnterprises_IdAndCity_Id(enterpriseId, cityId, pageable)
                .map(collaboratorMapper::toResponse);
    }

    // Listar colaborador por id
    public CollaboratorResponseDTO findById(Long id) {
        Collaborator collaborator = collaboratorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Colaborador não localizado"));
        return collaboratorMapper.toResponse(collaborator);
    }

    // Deletar um colaborador
    public void deleteCollaborator(Long id) {
        Collaborator collaborator = collaboratorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Colaborador não localizado"));
        collaboratorRepository.delete(collaborator);
    }

    // Atualizar o colaborador
    @Transactional
    public CollaboratorResponseDTO updateCollaborator(Long id, CollaboratorUpdateDTO updateDTO) {

        Collaborator collaborator = collaboratorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Colaborador não localizado"));

        if (updateDTO.name() != null && !updateDTO.name().isBlank()) {
            collaborator.setName(updateDTO.name());
        }
        if (updateDTO.rg() != null && !updateDTO.rg().isBlank()) {
            collaborator.setRg(updateDTO.rg());
        }
        if (updateDTO.dateOfBirth() != null) {
            validateAge(updateDTO.dateOfBirth());
            collaborator.setDate_of_birth(updateDTO.dateOfBirth());
        }
        if (updateDTO.address1() != null && !updateDTO.address1().isBlank()) {
            collaborator.setAddress1(updateDTO.address1());
        }
        if (updateDTO.address2() != null && !updateDTO.address2().isBlank()) {
            collaborator.setAddress2(updateDTO.address2());
        }
        if (updateDTO.pix() != null && !updateDTO.pix().isBlank()) {
            collaborator.setPix(updateDTO.pix());
        }
        if (updateDTO.fatherName() != null && !updateDTO.fatherName().isBlank()) {
            collaborator.setFatherName(updateDTO.fatherName());
        }
        if (updateDTO.fatherCpf() != null) {
            collaborator.setFatherCpf(updateDTO.fatherCpf());
        }
        if (updateDTO.motherName() != null && !updateDTO.motherName().isBlank()) {
            collaborator.setMotherName(updateDTO.motherName());
        }
        if (updateDTO.motherCpf() != null) {
            collaborator.setMotherCpf(updateDTO.motherCpf());
        }
        // transportMode pode ser null para remover
        if (updateDTO.transportMode() != null) {
            collaborator.setTransportMode(updateDTO.transportMode());
        }
        // Atualizar empresas se informado
        if (updateDTO.enterpriseIds() != null && !updateDTO.enterpriseIds().isEmpty()) {
            Set<Enterprise> enterprises = new HashSet<>();
            for (Long enterpriseId : updateDTO.enterpriseIds()) {
                Enterprise enterprise = enterpriseRepository.findById(enterpriseId)
                        .orElseThrow(() -> new ResourceNotFoundException("Empresa não localizada: " + enterpriseId));
                enterprises.add(enterprise);
            }
            collaborator.setEnterprises(enterprises);
        }
        // Atualizar a cidade
        if (updateDTO.cityId() != null) {
            City city = cityRepository.findById(updateDTO.cityId())
                    .orElseThrow(() -> new ResourceNotFoundException("Cidade não localizada"));
            collaborator.setCity(city);
        }

        Collaborator updated = collaboratorRepository.save(collaborator);
        return collaboratorMapper.toResponse(updated);
    }
}
