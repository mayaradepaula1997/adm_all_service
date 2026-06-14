package com.project.adm_all_service.mapper;

import com.project.adm_all_service.dtos.response.CollaboratorResponseDTO;
import com.project.adm_all_service.model.Collaborator;
import org.springframework.stereotype.Component;

//O Mapper é uma classe responsável por converter objetos de um tipo para outro

@Component //Pra que o Spring pode cria e gerenciar essa classe  e para que ela posso ser injetada em outras classes
public class CollaboratorMapper {

    //Conversão de entidade para DTO
    public CollaboratorResponseDTO toResponse(Collaborator collaborator){

        return new CollaboratorResponseDTO(
                collaborator.getId(),
                collaborator.getName(),
                collaborator.getCpf(),
                collaborator.getRg(),
                collaborator.getDate_of_birth(),
                collaborator.getAddress(),
                collaborator.getPix(),
                collaborator.getEnterprise().getName(),
                collaborator.getCity().getName(),
                collaborator.getCreation()
        );
    }
}
