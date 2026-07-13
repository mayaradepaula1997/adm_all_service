package com.project.adm_all_service.dtos.request;

import com.project.adm_all_service.enums.StatusLaunch;

import java.math.BigDecimal;

public record LaunchAppointmentUpdateDto(

        Long id,
        StatusLaunch statusLaunch,
        BigDecimal overtime,
        String observation
) {
}
