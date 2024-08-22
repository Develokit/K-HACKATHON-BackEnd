package com.develokit.maeum_ieum.util.api;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "공통 API 응답 구조")
public class ApiResult<T> {

    @Schema(description = "실제 응답 데이터")
    private final T data;
    private final boolean success;
    private final ApiError apiError;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private final LocalDateTime timeStamp = LocalDateTime.now();

    public ApiResult(boolean success, T data, ApiError apiError) {
        this.success = success;
        this.data = data;
        this.apiError = apiError;
    }
}
/*
1. 정상, 오류처리 모두 success 필드를 포함
 - 정상 처리라면 true, 오류 처리라면 false 값을 출력
2. 정상 처리는 data 필드를 포함하고 error 필드는 null
 - 응답 데이터는 객체로 표현
3. 오류 처리는 error 필드를 포함하고 data 필드는 null.  error 필드는 status, message 필드를 포함
 - status : HTTP Response status code 값과 동일한 값을 출력
 - message : 오류 메시지가 출력

 */