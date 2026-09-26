package com.project.adm_all_service.dtos.response;

import java.math.BigDecimal;

public record DashboardCollaboratorExpenseDto(
        Long collaboratorId,
        String collaboratorName,
        String cpf,
        Integer presenceDays,
        BigDecimal overtimeHours,
        BigDecimal totalExpense
) {}
