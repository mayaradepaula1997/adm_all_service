package com.project.adm_all_service.dtos.response;

import com.project.adm_all_service.enums.Role;

import java.util.List;
import java.util.Set;

public record UserResponseDto(Long id,
                              String name,
                              String email,
                              Set<Role> roleSet,
                              List<CitySimpleDto> cities,
                              List<EnterpriseSimpleDto> enterprises) {
}
