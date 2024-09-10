package com.develokit.maeum_ieum.handler;

import com.develokit.maeum_ieum.ex.CustomApiException;
import com.develokit.maeum_ieum.ex.CustomValidationException;
import com.develokit.maeum_ieum.util.ApiUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class CustomExceptionHandler {

    private final Logger log = LoggerFactory.getLogger(getClass());
    @ExceptionHandler(CustomApiException.class)
    public ResponseEntity<?> apiException(CustomApiException e){
        log.error(e.getMessage());
        return new ResponseEntity<>(ApiUtil.error(e.getMessage(), e.getCode()), e.getHttpStatus());
    }

    @ExceptionHandler(CustomValidationException.class)
    public ResponseEntity<?> validationException(CustomValidationException e){
        log.error(e.getMessage());
        return new ResponseEntity<>(ApiUtil.error(e.getMessage(), HttpStatus.BAD_REQUEST.value(), e.getErrorMap()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<?> handleMaxSizeException(MaxUploadSizeExceededException e) {
        Map<String, String> errorMap = new HashMap<>();
        errorMap.put("img", "파일 크기는 10MB를 초과할 수 없습니다.");
        return new ResponseEntity<>(ApiUtil.error(e.getMessage(), HttpStatus.BAD_REQUEST.value(), errorMap), HttpStatus.BAD_REQUEST);
    }
}
