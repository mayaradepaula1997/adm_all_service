package com.project.adm_all_service.exception;

//CLASSE UTILIZADA QUANDO VIOLAMOS UMA REGRA DE NEGOCIO

public class BusinessException  extends RuntimeException{

    //CONSTRUTOR
    public BusinessException(String message) {
        super(message);
    }
}
