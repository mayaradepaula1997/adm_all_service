package com.project.adm_all_service.enums;

public enum ClosingStatus {

    ABERTO("aberto"),
    FECHADO("fechado");

    private final String closingStatus;

    //CONSTRUTOR
    ClosingStatus(String closingStatus) {
        this.closingStatus = closingStatus;
    }

    //GETTER
    public String getClosingStatus() {
        return closingStatus;
    }
}
