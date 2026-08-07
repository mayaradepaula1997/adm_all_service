package com.project.adm_all_service.dtos.request;

import com.project.adm_all_service.enums.StatusLaunch;

import java.math.BigDecimal;

/**
 * DTO para atualização de um lançamento.
 * - id: ID do LaunchAppointment existente. Quando null, indica um novo lançamento a criar.
 * - collaboratorId: obrigatório quando id é null (novo colaborador no apontamento).
 */
public record LaunchAppointmentUpdateDto(

        Long id,
        Long collaboratorId,
        StatusLaunch statusLaunch,
        BigDecimal overtime,
        String observation
) {
}

