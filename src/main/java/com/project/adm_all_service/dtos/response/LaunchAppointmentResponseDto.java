package com.project.adm_all_service.dtos.response;

import com.project.adm_all_service.enums.StatusLaunch;

import java.math.BigDecimal;

public record LaunchAppointmentResponseDto(

        Long id,
        Long collaboratorId,
        StatusLaunch statusLaunch,
        BigDecimal overtime,
        String observation
) {
}
