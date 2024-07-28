package com.develokit.maeum_ieum.util;

import com.develokit.maeum_ieum.util.api.ApiError;
import com.develokit.maeum_ieum.util.api.ApiResult;
import org.springframework.http.HttpStatus;

import java.util.Map;

public class ApiUtil<T> {

    public static <T>ApiResult<T> success(T data){
        return new ApiResult<>(true, data, null);
    }

    public static <T>ApiResult<T> error(String msg, int status){
        return new ApiResult<>(false, null, new ApiError(status, msg));
    }
    public static <T>ApiResult<T> error(String msg, int status, T errorMap){
        return new ApiResult<>(false, errorMap, new ApiError(status, msg));
    }
}
