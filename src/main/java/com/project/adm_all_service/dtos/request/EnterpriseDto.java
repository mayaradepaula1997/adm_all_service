package com.project.adm_all_service.dtos.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record EnterpriseDto(Long id,
                            @NotBlank(message = "O nome da empresa é obrigatiório")
                            String name,
                            @NotBlank(message = "O campo CNPJ é obrigatório")
                            String cnpj,
                            @NotNull(message = "Informe o id da empresa")
                            Long cityId) {
}
