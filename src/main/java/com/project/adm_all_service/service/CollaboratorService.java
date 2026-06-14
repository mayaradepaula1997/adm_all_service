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

@Service
public class CollaboratorService {

    private final CollaboratorRepository collaboratorRepository;

    private final CityRepository cityRepository;

    private final EnterpriseRepository enterpriseRepository;

    private final CollaboratorMapper collaboratorMapper;

    //Construtor
    public CollaboratorService(CollaboratorRepository collaboratorRepository, CityRepository cityRepository, EnterpriseRepository enterpriseRepository, CollaboratorMapper collaboratorMapper) {
        this.collaboratorRepository = collaboratorRepository;
        this.cityRepository = cityRepository;
        this.enterpriseRepository = enterpriseRepository;
        this.collaboratorMapper = collaboratorMapper;
    }

    //VALIDAÇÃO DA IDADE
    private void validateAge(LocalDate birthDate){

        int age = Period.between(         //Fazer a comparação da data atual e a data enviada para o usuário
                birthDate,
                LocalDate.now()
        ).getYears();

        if (age < 18){
            throw new BusinessException("O colaborador deve possuir pelo menos 18 anos");
        }
    }


    //VALIDAÇÃO DO CPF UNICO
    private void validateCpf(String cpf){

        if (collaboratorRepository.existsByCpf(cpf)){
            throw new BusinessException("Já existe um colaborador com esse CPF");
        }
    }

    public CollaboratorResponseDTO create (CollaboratorRequestDTO dto){

        validateCpf(dto.cpf());
        validateAge(dto.dateOfBirth());

        //Verificar se a empresa já existe
        Enterprise enterprise = enterpriseRepository.findById(dto.enterpriseId())
                .orElseThrow(()-> new ResourceNotFoundException("Empresa não encontrada"));

        //Verificando de a cidade existe
        City city = cityRepository.findById(dto.cityId())
                .orElseThrow(()-> new ResourceNotFoundException("Cidade não encontrada"));


        Collaborator collaborator = new Collaborator();

        collaborator.setName(dto.name());
        collaborator.setCpf(dto.cpf());
        collaborator.setRg(dto.rg());
        collaborator.setDate_of_birth(dto.dateOfBirth());
        collaborator.setAddress(dto.address());
        collaborator.setPix(dto.pix());
        collaborator.setEnterprise(enterprise);
        collaborator.setCity(city);

        Collaborator saved = collaboratorRepository.save(collaborator);

        //Utilizando o Mapper para converter que entidade para dto
        return collaboratorMapper.toResponse(collaborator);
    }

    //Listar todos os colaboradores por paginação
    public Page<CollaboratorResponseDTO> listAll(int page, int size){

        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());

        return collaboratorRepository.findAll(pageable)
                .map(collaboratorMapper::toResponse);
    }

    //Listar colaborador por id
    public CollaboratorResponseDTO findById(Long id){

        //Buscamos o colaborador no BD para verificar se ele existe
        Collaborator collaborator = collaboratorRepository.findById(id)
                .orElseThrow(()-> new  ResourceNotFoundException("Colaborador não localizado"));

        //Conversão de entidade para DTO
        return collaboratorMapper.toResponse(collaborator);
    }

    //Deletar um colaborador
    public void deleteCollaborator (Long id){

        //Verificar se esse colaborador existe no BD
        Collaborator collaborator = collaboratorRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Colaborador não localizado"));

        //Deleta esse colaborador do DB
        collaboratorRepository.delete(collaborator);
    }

    //Atualizar o colaborador
    @Transactional
    public CollaboratorResponseDTO updateCollaborator(Long id, CollaboratorUpdateDTO updateDTO){

        //Buscamos o colaborador do BD
        Collaborator collaborator = collaboratorRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Colaborador não localizado"));

        //Atualizar nome
        if (updateDTO.name() != null && !updateDTO.name().isBlank()){
            collaborator.setName(updateDTO.name());
        }

        //Atualizar RG
        if (updateDTO.rg()!= null && !updateDTO.rg().isBlank()){
            collaborator.setRg(updateDTO.rg());
        }

        //Atualizar aniversario
        if (updateDTO.dateOfBirth()!= null){

            validateAge(updateDTO.dateOfBirth()); //validação da data de aniversario
            collaborator.setDate_of_birth(updateDTO.dateOfBirth());
        }
        //Atualizar endereço
        if (updateDTO.address()!= null && !updateDTO.address().isBlank()){
            collaborator.setAddress(updateDTO.address());
        }

        //Atualizar pix
        if (updateDTO.pix()!= null && !updateDTO.pix().isBlank()){
            collaborator.setPix(updateDTO.pix());
        }

        //Atualizar empresa
        if (updateDTO.cityId()!= null){

            //Buscamos a empresa no BD
            Enterprise enterprise = enterpriseRepository.findById(updateDTO.cityId())
                    .orElseThrow(()-> new ResourceNotFoundException("Empresa não localizada"));

            collaborator.setEnterprise(enterprise);
        }

        //Atualizar a cidade
        if (updateDTO.cityId() != null){

            //Buscamos a cidade no BD
            City city = cityRepository.findById(updateDTO.cityId())
                    .orElseThrow(()-> new ResourceNotFoundException("Cidade não localizada"));

            collaborator.setCity(city);

        }
        //Salva todas as alterações no BD
        Collaborator collaboratorUpdate = collaboratorRepository.save(collaborator);

        //Retorna do método, que vai transforma uma entidade em um DTO
        return collaboratorMapper.toResponse(collaboratorUpdate);
    }
}
