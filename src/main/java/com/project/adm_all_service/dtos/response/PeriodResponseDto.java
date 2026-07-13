package com.project.adm_all_service.dtos.response;

import com.project.adm_all_service.enums.Fortnight;

import java.time.LocalDate;

public record PeriodResponseDto(

        Long id,
        Integer year,
        Integer month,
        Fortnight fortnight,
        LocalDate startDate,
        LocalDate endDate
) {
}
