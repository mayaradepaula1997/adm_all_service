package com.project.adm_all_service.dtos.response;

import com.project.adm_all_service.model.Enterprise;


import java.time.LocalDateTime;
import java.util.List;

public record CityCreateDto(Long id,
                            String name,
                            String uf,
                            LocalDateTime creation,
                            List<Enterprise> enterprises) {
}
