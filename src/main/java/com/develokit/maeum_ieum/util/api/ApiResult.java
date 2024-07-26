package com.develokit.maeum_ieum.util.api;

import lombok.Data;

@Data
public class ApiResult<T> {

    private final T data;
    private final boolean success;
    private final ApiError apiError;

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