package com.project.adm_all_service.exception;

//CLASSE UTILIZADA COM O OBJETO NÃO É ENCONTRADO
//RUNTIMEEXCEPTION - NÃO PRECISA SER TRATADO COM TRY/CATH

public class ResourceNotFoundException extends  RuntimeException{

    //CONSTRUTOR
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
