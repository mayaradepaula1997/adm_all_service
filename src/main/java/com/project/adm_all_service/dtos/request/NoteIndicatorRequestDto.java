package com.project.adm_all_service.dtos.request;

import com.project.adm_all_service.enums.Fortnight;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;

public record NoteIndicatorRequestDto(

        @NotNull(message = "A cidade é obrigatória")
        Long cityId,

        @NotNull(message = "A empresa é obrigatória")
        Long enterpriseId,

        @NotNull(message = "A data do apontamento é obrigatória.")
        LocalDate appointmentDate,

        @NotNull(message = "A quinzena é obrigatória.")
        Fortnight fortnight,

       List<LaunchAppointmentRequestDto> launchAppointments

) {
}