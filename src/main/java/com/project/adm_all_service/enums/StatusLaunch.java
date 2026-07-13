package com.project.adm_all_service.enums;

public enum StatusLaunch {

    PRESENCE("presence"),
    ABSENCE ("absence");

    //Atributo
    private final String statusLaunch;

    //Construtor
    StatusLaunch(String statusLaunch) {
        this.statusLaunch = statusLaunch;
    }

    //Getter
    public String getStatusLaunch() {
        return statusLaunch;
    }
}
