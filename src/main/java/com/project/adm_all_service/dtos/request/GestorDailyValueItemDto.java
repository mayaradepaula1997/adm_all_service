package com.project.adm_all_service.dtos.request;

import java.math.BigDecimal;

public record GestorDailyValueItemDto(
        Long collaboratorId,
        String date,        // formato: yyyy-MM-dd
        BigDecimal dailyValue
) {
}
