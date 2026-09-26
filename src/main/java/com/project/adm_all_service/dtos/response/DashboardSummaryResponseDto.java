package com.project.adm_all_service.dtos.response;

import java.math.BigDecimal;
import java.util.List;

public record DashboardSummaryResponseDto(
    List<DashboardExpenseDto> expenses,
    BigDecimal globalTotal
) {}
