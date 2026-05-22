package com.project.adm_all_service.security;

import com.project.adm_all_service.model.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils {

    public static Long getUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !(auth.getPrincipal() instanceof User user)) {
            throw new RuntimeException("Usuário não autenticado");
        }

        return user.getId();
    }
}
