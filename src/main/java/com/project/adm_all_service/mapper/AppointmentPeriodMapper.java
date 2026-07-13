package com.project.adm_all_service.mapper;

import com.project.adm_all_service.dtos.request.PeriodRequestDto;
import com.project.adm_all_service.dtos.response.PeriodResponseDto;
import com.project.adm_all_service.model.AppointmentPeriod;

public class AppointmentPeriodMapper {

    //Construtor vazio
    public AppointmentPeriodMapper() {
    }

    //toEntity
    public static AppointmentPeriod toEntity(PeriodRequestDto dto){

        AppointmentPeriod appointmentPeriod = new AppointmentPeriod();
        appointmentPeriod.setYear(dto.year());
        appointmentPeriod.setMonth(dto.month());
        appointmentPeriod.setFortnight(dto.fortnight());

        return appointmentPeriod;
    }

    //toDTO
    public static PeriodResponseDto toDto (AppointmentPeriod period){

        return new PeriodResponseDto(
                period.getId(),
                period.getYear(),
                period.getMonth(),
                period.getFortnight(),
                period.getStartDate(),
                period.getEndDate()
        );
    }
}
