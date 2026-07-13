package com.project.adm_all_service.dtos.request;

import com.project.adm_all_service.enums.StatusLaunch;

import java.math.BigDecimal;

public record LaunchAppointmentRequestDto(

        Long collaboratorId,
        StatusLaunch statusLaunch,
        BigDecimal overtime,
        String observation

) {
}
