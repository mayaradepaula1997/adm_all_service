package com.project.adm_all_service.dtos.request;

import java.time.LocalDate;

public record CollaboratorUpdateDTO(String name,
                                    String rg,
                                    LocalDate dateOfBirth,
                                    String address,
                                    String pix,
                                    Long enterpriseId,
                                    Long cityId) {
}
