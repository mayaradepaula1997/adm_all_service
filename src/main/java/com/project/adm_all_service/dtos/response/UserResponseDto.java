package com.project.adm_all_service.dtos.response;

import com.project.adm_all_service.enums.Role;

import java.util.Set;

public record UserResponseDto(Long id,
                              String name,
                              String email,
                              Set<Role>roleSet,
                              String cityName,
                              String enterpriseName) {
}
