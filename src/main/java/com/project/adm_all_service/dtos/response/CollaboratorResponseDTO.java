package com.project.adm_all_service.dtos.response;

import com.project.adm_all_service.enums.TransportMode;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record CollaboratorResponseDTO(
        Long id,
        String name,
        String cpf,
        String rg,
        LocalDate dateOfBirth,
        String address1,
        String address2,
        String pix,
        String fatherName,
        String fatherCpf,
        String motherName,
        String motherCpf,
        TransportMode transportMode,
        List<EnterpriseSimpleDto> enterprises,
        String cityName,
        Long cityId,
        LocalDateTime creation
) {
}
