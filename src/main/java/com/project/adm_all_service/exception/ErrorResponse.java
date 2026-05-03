package com.project.adm_all_service.exception;

//CLASSE QUE VAI PADRONIZAR A RESPOSTA DO ERRO

import java.time.LocalDateTime;

public class ErrorResponse {

    private LocalDateTime timestamp; //DATA E HORA DO ERRO
    private int status;  //STATUS HTTP: 404, 500, 400
    private String error;
    private String path;  //ENDPOINT QUE GEROU O ERRO

    //CONSTRUTOR
    public ErrorResponse(LocalDateTime timestamp, int status, String error, String path) {
        this.timestamp = timestamp;
        this.status = status;
        this.error = error;
        this.path = path;
    }

    //GETTER AND SETTER
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
