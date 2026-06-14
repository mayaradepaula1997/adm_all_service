package com.project.adm_all_service.dtos.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.br.CPF;

import java.time.LocalDate;

public record CollaboratorRequestDTO(

        @NotBlank(message = "Nome obrigatório")
        String name,

        @CPF(message = "CPF inválido")
        @NotBlank(message = "CPF obrigatório")
        String cpf,

        @NotBlank(message = "RG obrigatório")
        String rg,

        @NotNull(message = "Data de nascimento obrigatória")
        LocalDate dateOfBirth,

        @NotBlank(message = "Endereço obrigatório")
        String address,

        @NotBlank(message = "PIX obrigatório")
        String pix,

        @NotNull(message = "Empresa obrigatória")
        Long enterpriseId,

        @NotNull(message = "Cidade obrigatória")
        Long cityId

) {
}
