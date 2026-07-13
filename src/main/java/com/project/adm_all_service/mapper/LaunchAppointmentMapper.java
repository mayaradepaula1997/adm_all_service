package com.project.adm_all_service.mapper;

import com.project.adm_all_service.dtos.request.LaunchAppointmentRequestDto;
import com.project.adm_all_service.dtos.response.LaunchAppointmentResponseDto;
import com.project.adm_all_service.model.Collaborator;
import com.project.adm_all_service.model.LaunchAppointment;

public class LaunchAppointmentMapper {

    //Construtor  Vazio
    public LaunchAppointmentMapper() {
    }

    //toEntity
    public static LaunchAppointment toEntity(LaunchAppointmentRequestDto dto, Collaborator collaborator){

        LaunchAppointment launchAppointment = new LaunchAppointment();
        launchAppointment.setCollaborator(collaborator);
        launchAppointment.setStatusLaunch(dto.statusLaunch());
        launchAppointment.setOvertime(dto.overtime());
        launchAppointment.setObservation(dto.observation());

        return launchAppointment;

    }

    //toDto
    public static LaunchAppointmentResponseDto toDto(LaunchAppointment launchAppointment){

        return  new LaunchAppointmentResponseDto(
                launchAppointment.getId(),
                launchAppointment.getCollaborator().getId(),
                launchAppointment.getStatusLaunch(),
                launchAppointment.getOvertime(),
                launchAppointment.getObservation()
        );
    }
}
