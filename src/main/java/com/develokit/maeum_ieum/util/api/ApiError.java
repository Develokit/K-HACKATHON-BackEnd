package com.develokit.maeum_ieum.util.api;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ApiError {

    private final int status;
    private final String msg;

    public ApiError(int status, String msg) {
        this.status = status;
        this.msg = msg;
    }

}
