package com.project.adm_all_service.dtos.response;

import java.math.BigDecimal;

public record DashboardExpenseDto(
    Long enterpriseId,
    String enterpriseName,
    BigDecimal totalExpense
) {}
