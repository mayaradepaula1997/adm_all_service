package com.project.adm_all_service.enums;

public enum TypeApontamento {

    PRESENCA("presença"),
    FALTA("falta"),
    DIARIA("diária");

    private final String typeApontamento;

    //CONSTRUTOR
    TypeApontamento(String type_apontamento) {
        this.typeApontamento = type_apontamento;
    }

    //GETTER
    public String getType_apontamento() {
        return typeApontamento;
    }
}
