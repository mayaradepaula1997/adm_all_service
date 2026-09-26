package com.project.adm_all_service.dtos.response;

import java.util.List;

public record DashboardEvolutionDto(
    String period,
    Integer year,
    Integer month,
    Integer fortnight,
    List<DashboardExpenseDto> expenses
) {}
