package com.project.adm_all_service.mapper;

import com.project.adm_all_service.dtos.response.NoteIndicatorResponseDto;
import com.project.adm_all_service.model.*;

import java.time.LocalDate;

public class NoteIndicatorMapper {

    //Construtor vazio
    public NoteIndicatorMapper() {
    }
    //toEntity
    public static NoteIndicator toEntity (City city,
                                          Enterprise enterprise,
                                          User createdBy,
                                          AppointmentPeriod appointmentPeriod,
                                          LocalDate appointmentDate){

        NoteIndicator noteIndicator = new NoteIndicator();
        noteIndicator.setCity(city);
        noteIndicator.setEnterprise(enterprise);
        noteIndicator.setCreatedBy(createdBy);
        noteIndicator.setAppointmentPeriod(appointmentPeriod);
        noteIndicator.setAppointmentDate(appointmentDate);

        return noteIndicator;
    }
    //toDto
    public static NoteIndicatorResponseDto toDto(NoteIndicator noteIndicator){

        return new NoteIndicatorResponseDto(
                noteIndicator.getId(),
                noteIndicator.getCity().getId(),
                noteIndicator.getEnterprise().getId(),
                noteIndicator.getCreatedBy().getId(),

                noteIndicator.getAppointmentDate(),
                noteIndicator.getDatecreation(),

                AppointmentPeriodMapper.toDto(
                        noteIndicator.getAppointmentPeriod()
                ),

                noteIndicator.getLaunchAppointments()
                        .stream()
                        .map(LaunchAppointmentMapper::toDto)
                        .toList()
        );
    }
}
