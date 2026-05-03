package com.project.adm_all_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;

@ControllerAdvice   //Essa anotação é um "guardião" que vai interceptar  exeções lançadas no controller
public class GlobalExceptionHandler {

    //Esse método é lançado quando o recurso não é encontrado
    @ExceptionHandler(ResourceNotFoundException.class) //Recurso não encontrado - É lançado esse método
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException ex, WebRequest request){ //ResourceNotFoundException: representa a exceção capturada, WebRequest: contém a URl que foi chamada

        ErrorResponse error = new ErrorResponse(

                LocalDateTime.now(),
                HttpStatus.NO_CONTENT.value(),
                ex.getMessage(),
                request.getDescription(false)
        );

        return new ResponseEntity<>(error,HttpStatus.NOT_FOUND);
    }

    //Quando é violado alguma regra de negócio
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException (BusinessException ex, WebRequest request){

        ErrorResponse error = new ErrorResponse(

                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(),
                request.getDescription(false)
        );

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    //Quando ocorre algo erro inesperado
    @ExceptionHandler(Exception.class)//Captura qualquer exceção não tratada nos métodos a cima, qualquer erro inesperado
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex, WebRequest request){

        ErrorResponse error = new ErrorResponse(

                LocalDateTime.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Error interno no servidor",
                request.getDescription(false)
        );

        return new ResponseEntity<>(error,HttpStatus.INTERNAL_SERVER_ERROR);

    }

}
