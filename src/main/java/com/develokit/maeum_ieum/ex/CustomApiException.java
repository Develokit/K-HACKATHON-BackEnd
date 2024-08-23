package com.develokit.maeum_ieum.ex;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class CustomApiException extends RuntimeException{

    private int code;
    private HttpStatus httpStatus;
    public CustomApiException(String message){super(message);};

    public CustomApiException(String message, int code, HttpStatus httpStatus){
        super(message);
        this.code = code;
        this.httpStatus = httpStatus;
    }

}
