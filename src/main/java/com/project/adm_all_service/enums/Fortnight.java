package com.project.adm_all_service.enums;

//QUINZENA
public enum Fortnight {

    FIRST("first"),
    SECOND("second");

    //Atributo - Sua descrição
    private final  String fortnight;

    //Construtor
    Fortnight(String fortnight) {
        this.fortnight = fortnight;
    }

    //Getter
    public String getFortnight() {
        return fortnight;
    }
}
