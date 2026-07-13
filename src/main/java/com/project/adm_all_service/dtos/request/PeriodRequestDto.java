package com.project.adm_all_service.dtos.request;

import com.project.adm_all_service.enums.Fortnight;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record PeriodRequestDto(

        @NotNull(message = "O ano é obrigatório.")
        Integer year,

        @NotNull(message = "O mês é obrigatório.")
        @Min(value = 1, message = "O mês deve ser entre 1 e 12.")
        @Max(value = 12, message = "O mês deve ser entre 1 e 12.")
        Integer month,

        @NotNull(message = "O quinzena é obrigatória.")
        Fortnight fortnight
) {
}
