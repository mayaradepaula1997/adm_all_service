package com.project.adm_all_service.dtos.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record ClosingResponseDto(
        Long cityId,
        String cityName,
        Long enterpriseId,
        String enterpriseName,
        Integer month,
        Integer year,
        Integer fortnight,
        LocalDate periodStart,
        LocalDate periodEnd,
        BigDecimal defaultDailyValue,
        boolean closed,
        String closedAt,
        List<CollaboratorClosingDto> collaborators,
        BigDecimal totalDiarias,
        BigDecimal totalOvertimeValue,
        BigDecimal grandTotal
) {

    public record CollaboratorClosingDto(
            Long collaboratorId,
            String collaboratorName,
            String cpf,
            String pix,
            List<DayEntryDto> days,
            int totalDays,
            BigDecimal dailyValue,
            BigDecimal totalDailyValue,
            BigDecimal overtimeHours,
            BigDecimal overtimeValue,
            BigDecimal total
    ) {
    }

    public record DayEntryDto(
            LocalDate date,
            String status,   // PRESENCE, ABSENCE, FALTA
            BigDecimal overtimeHours,
            String observation,
            BigDecimal dailyValue    // valor da diária definido pelo gestor para este dia
    ) {
    }
}
