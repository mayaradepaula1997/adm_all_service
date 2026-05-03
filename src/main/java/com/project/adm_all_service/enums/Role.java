package com.project.adm_all_service.enums;

public enum Role {

    ADMIN_MASTER("admin_master"),
    RH("rh"),
    GESTOR("gestor"),
    APONTADOR("apontador");

    private final String role;

    //Construtor
    Role(String role) {
        this.role = role;
    }

    //Getter
    public String getRole() {
        return role;
    }
}
