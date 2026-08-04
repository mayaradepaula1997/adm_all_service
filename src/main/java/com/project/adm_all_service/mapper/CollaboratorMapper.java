package com.project.adm_all_service.mapper;

import com.project.adm_all_service.dtos.response.CollaboratorResponseDTO;
import com.project.adm_all_service.dtos.response.EnterpriseSimpleDto;
import com.project.adm_all_service.model.Collaborator;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CollaboratorMapper {

    public CollaboratorResponseDTO toResponse(Collaborator collaborator) {

        List<EnterpriseSimpleDto> enterprises = collaborator.getEnterprises()
                .stream()
                .map(e -> new EnterpriseSimpleDto(e.getId(), e.getName(), e.getDocumento()))
                .toList();

        return new CollaboratorResponseDTO(
                collaborator.getId(),
                collaborator.getName(),
                collaborator.getCpf(),
                collaborator.getRg(),
                collaborator.getDate_of_birth(),
                collaborator.getAddress1(),
                        collaborator.getAddress2(),
                collaborator.getPix(),
                collaborator.getFatherName(),
                        collaborator.getFatherCpf(),
                collaborator.getMotherName(),
                collaborator.getMotherCpf(),
                collaborator.getTransportMode(),
                enterprises,
                collaborator.getCity() != null ? collaborator.getCity().getName() : null,
                collaborator.getCity() != null ? collaborator.getCity().getId() : null,
                collaborator.getCreation()
        );
    }
}
