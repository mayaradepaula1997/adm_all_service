package com.project.adm_all_service.dtos.response;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record NoteIndicatorResponseDto(

        Long id,
        Long cityId,
        Long enterpriseId,
        Long createdById,
        LocalDate appointmentDate,
        LocalDateTime dateCreation,
        PeriodResponseDto periodResponseDto,
        List<LaunchAppointmentResponseDto> launchAppointments
) {
}
