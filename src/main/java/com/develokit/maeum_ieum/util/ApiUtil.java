package com.develokit.maeum_ieum.util;

import com.develokit.maeum_ieum.util.api.ApiError;
import com.develokit.maeum_ieum.util.api.ApiResult;
import org.springframework.http.HttpStatus;

public class ApiUtil<T> {

    public static <T>ApiResult<T> success(T data){
        return new ApiResult<>(true, data, null);
    }

    public static <T>ApiResult<T> error(String msg, int status){
        return new ApiResult<>(false, null, new ApiError(status, msg));
    }
}
