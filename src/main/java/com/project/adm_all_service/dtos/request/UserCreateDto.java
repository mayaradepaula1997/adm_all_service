package com.project.adm_all_service.dtos.request;

import com.project.adm_all_service.enums.Role;

import java.util.Set;

public record UserCreateDto(String name,
                            String email,
                            String password,
                            Set<Role> roleSet,
                            Long cityId,
                            Long enterpriseId) {


}
