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

    // Criação da empresa
    public EnterpriseCreateDto create(EnterpriseDto enterpriseDto) {

        City city = cityRepository.findById(enterpriseDto.cityId())
                .orElseThrow(() -> new ResourceNotFoundException("Cidade não encontrada"));

        Optional<Enterprise> enterprise = enterpriseRepository.findByDocumento(enterpriseDto.documento());
        if (enterprise.isPresent()) {
            throw new BusinessException("Empresa já existe com esse documento");
        }

        Enterprise enterpriseNew = new Enterprise();
        enterpriseNew.setName(enterpriseDto.name());
        enterpriseNew.setDocumento(enterpriseDto.documento());
        enterpriseNew.setCreation(LocalDateTime.now());
        enterpriseNew.setCity(city);

        Enterprise save = enterpriseRepository.save(enterpriseNew);

        return new EnterpriseCreateDto(
                save.getId(),
                save.getName(),
                save.getDocumento(),
                save.getCreation(),
                save.getCity().getName()
        );
    }

    // Lista todas as empresas
    public Page<EnterpriseDto> listAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return enterpriseRepository.findAll(pageable)
                .map(e -> new EnterpriseDto(e.getId(), e.getName(), e.getDocumento(), e.getCity().getId()));
    }

    // Lista empresa pelo id
    public EnterpriseDto findById(Long id) {
        Enterprise enterprise = enterpriseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Empresa não encontrada"));
        return new EnterpriseDto(enterprise.getId(), enterprise.getName(), enterprise.getDocumento(), enterprise.getCity().getId());
    }

    // Deletar empresa
    public void delete(Long id) {
        Enterprise enterprise = enterpriseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Empresa não encontrada"));
        enterpriseRepository.delete(enterprise);
    }

    // Atualizar empresa
    public EnterpriseDto update(Long id, EnterpriseUpdateDto enterpriseUpdateDto) {
        Enterprise enterprise = enterpriseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Empresa não encontrada"));

        if (enterpriseUpdateDto.name() != null && !enterpriseUpdateDto.name().isBlank()) {
            enterprise.setName(enterpriseUpdateDto.name());
        }
        if (enterpriseUpdateDto.cityId() != null) {
            City city = cityRepository.findById(enterpriseUpdateDto.cityId())
                    .orElseThrow(() -> new ResourceNotFoundException("Cidade não encontrada"));
            enterprise.setCity(city);
        }

        enterpriseRepository.save(enterprise);
        return new EnterpriseDto(enterprise.getId(), enterprise.getName(), enterprise.getDocumento(), enterprise.getCity().getId());
    }
}
