package com.develokit.maeum_ieum.util.api;

import lombok.Data;

@Data
public class ApiError {

    private final int status;
    private final String msg;

    public ApiError(int status, String msg) {
        this.status = status;
        this.msg = msg;
    }
}
