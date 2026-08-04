package com.project.adm_all_service.dtos.request;

import com.project.adm_all_service.enums.TransportMode;

import java.time.LocalDate;
import java.util.Set;

public record CollaboratorUpdateDTO(
        String name,
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
        Set<Long> enterpriseIds,
        Long cityId
) {
}
