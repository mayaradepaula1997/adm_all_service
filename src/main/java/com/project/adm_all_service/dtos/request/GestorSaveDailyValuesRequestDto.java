package com.project.adm_all_service.dtos.request;

import java.util.List;

public record GestorSaveDailyValuesRequestDto(
        Long cityId,
        Long enterpriseId,
        Integer month,
        Integer year,
        Integer fortnight,
        List<GestorDailyValueItemDto> dailyValues
) {
}
