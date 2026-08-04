package com.project.adm_all_service.dtos.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record EnterpriseDto(Long id,
                            @NotBlank(message = "O nome da empresa é obrigatório")
                            String name,
                            @NotBlank(message = "O campo Documento (CPF/CNPJ) é obrigatório")
                            String documento,
                            @NotNull(message = "Informe o id da cidade")
                            Long cityId) {
}
