package com.project.adm_all_service.dtos.request;

import com.project.adm_all_service.enums.StatusLaunch;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record LaunchAppointmentRequestDto(

        @NotNull(message = "O colaborador é obrigatório")
        Long collaboratorId,

        @NotNull(message = "O status é obrigatório.")
        StatusLaunch statusLaunch,

        BigDecimal overtime,

        String observation

) {
}
