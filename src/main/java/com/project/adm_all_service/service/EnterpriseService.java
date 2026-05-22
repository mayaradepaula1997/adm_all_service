package com.project.adm_all_service.service;

import com.project.adm_all_service.dtos.request.EnterpriseDto;
import com.project.adm_all_service.dtos.request.EnterpriseUpdateDto;
import com.project.adm_all_service.dtos.response.CityCreateDto;
import com.project.adm_all_service.dtos.response.EnterpriseCreateDto;
import com.project.adm_all_service.exception.BusinessException;
import com.project.adm_all_service.exception.ResourceNotFoundException;
import com.project.adm_all_service.model.City;
import com.project.adm_all_service.model.Enterprise;
import com.project.adm_all_service.repository.CityRepository;
import com.project.adm_all_service.repository.EnterpriseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.swing.border.EmptyBorder;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class EnterpriseService {

    private final CityRepository cityRepository;

    private final EnterpriseRepository enterpriseRepository;

    public EnterpriseService(CityRepository cityRepository, EnterpriseRepository enterpriseRepository) {
        this.cityRepository = cityRepository;
        this.enterpriseRepository = enterpriseRepository;
    }

    //Criação da empresa
    public EnterpriseCreateDto create (EnterpriseDto enterpriseDto){

        //Busca primeiro a cidade, para verificar se ela existe
        City city = cityRepository.findById(enterpriseDto.cityId())
                .orElseThrow(() -> new ResourceNotFoundException("Cidade não encontrada"));

        //Buscamos a empresa no BD pra verificar se ela já existe
        Optional<Enterprise> enterprise = enterpriseRepository.findByCnpj(enterpriseDto.cnpj());

        if (enterprise.isPresent()){
            throw new BusinessException("Empresa já existe");
        }
        //Instacia da classe
       Enterprise enterpriseNew = new Enterprise();
        enterpriseNew.setName(enterpriseDto.name());
        enterpriseNew.setCnpj(enterpriseDto.cnpj());
        enterpriseNew.setCreation(LocalDateTime.now());
        enterpriseNew.setCity(city);

        //Salva no banco de dados
        Enterprise save = enterpriseRepository.save(enterpriseNew);

        //Retorna o dto
        return new EnterpriseCreateDto(
                save.getId(),
                save.getName(),
                save.getCnpj(),
                save.getCreation(),
                save.getCity().getName()
        );


    }

    //Lista todas as empresas
    public Page<EnterpriseDto> listAll(int page, int size){

        Pageable pageable = PageRequest.of(page, size);

        return enterpriseRepository.findAll(pageable) //Vamos buscar todas a empresas no banco de dados
                .map(enterprise -> new EnterpriseDto( //Para cada empresa que retorna, vamos instanciar o DTO e retorna valores especificos
                        enterprise.getId(),
                        enterprise.getName(),
                        enterprise.getCnpj(),
                        enterprise.getCity().getId()));

    }

    //Lista empresa pelo id
    public EnterpriseDto findById (Long id){

        Enterprise enterprise = enterpriseRepository.findById(id)  //OBS: Quando retornar esse tipo de exeção não preciso retorna uma optinal
                .orElseThrow(() -> new ResourceNotFoundException("Empresa não encontrada"));

        return new EnterpriseDto(
                enterprise.getId(),
                enterprise.getName(),
                enterprise.getCnpj(),
                enterprise.getCity().getId()
        );
    }

    //Deletar empresa
    public void delete (Long id){

        Enterprise enterprise = enterpriseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Empresa não encontrada"));

        enterpriseRepository.delete(enterprise);
    }

    //Atualizar empresa
    public EnterpriseDto update(Long id, EnterpriseUpdateDto enterpriseUpdateDto) {

        Enterprise enterprise = enterpriseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Empresa não encontrada"));

        if (enterpriseUpdateDto.name() != null && !enterpriseUpdateDto.name().isBlank()){

            enterprise.setName(enterpriseUpdateDto.name());
        }

        if (enterpriseUpdateDto.cityId() != null){

            //Tambem precisamos vericar se o id da cidade que vai ser atualizada existe
            City city = cityRepository.findById(enterpriseUpdateDto.cityId())
                    .orElseThrow(() -> new ResourceNotFoundException("Cidade não encontrada"));

            enterprise.setCity(city);
        }

        enterpriseRepository.save(enterprise);

        return new EnterpriseDto(
                enterprise.getId(),
                enterprise.getName(),
                enterprise.getCnpj(),
                enterpriseUpdateDto.cityId());


    }
}
