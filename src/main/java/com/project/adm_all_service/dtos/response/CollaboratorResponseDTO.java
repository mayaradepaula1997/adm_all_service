package com.project.adm_all_service.dtos.response;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record CollaboratorResponseDTO(Long id,
                                      String name,
                                      String cpf,
                                      String rg,
                                      LocalDate dateOfBirth,
                                      String address,
                                      String pix,
                                      String enterpriseName,
                                      String cityName,
                                      LocalDateTime creation) {

}
