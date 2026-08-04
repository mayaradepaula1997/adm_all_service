package com.project.adm_all_service.dtos.response;

import java.time.LocalDateTime;

public record EnterpriseCreateDto(Long id, String name, String documento, LocalDateTime creation, String cityName) {
}
