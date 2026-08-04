package com.project.adm_all_service.dtos.request;

import com.project.adm_all_service.enums.TransportMode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.br.CPF;

import java.time.LocalDate;
import java.util.Set;

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

        @NotBlank(message = "Endereço 1 é obrigatório")
        String address1,

        @NotBlank(message = "Endereço 2 é obrigatório")
        String address2,

        @NotBlank(message = "PIX obrigatório")
        String pix,

        @NotBlank(message = "Nome do pai obrigatório")
        String fatherName,

        @CPF(message = "CPF do pai inválido")
        String fatherCpf,

        @NotBlank(message = "Nome da mãe obrigatório")
        String motherName,

        @CPF(message = "CPF da mãe inválido")
        String motherCpf,

        // Opcional
        TransportMode transportMode,

        @NotNull(message = "Pelo menos uma empresa é obrigatória")
        Set<Long> enterpriseIds,

        @NotNull(message = "Cidade obrigatória")
        Long cityId

) {
}
