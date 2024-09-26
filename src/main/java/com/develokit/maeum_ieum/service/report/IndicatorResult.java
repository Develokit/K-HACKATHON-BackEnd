package com.develokit.maeum_ieum.service.report;

//지표 상수 valu와 enum
public class IndicatorResult {
    private final String value;
    private final String reason;

    public IndicatorResult(String value, String reason) {
        this.value = value;
        this.reason = reason;
    }

    public String getValue() {
        return value;
    }

    public String getReason() {
        return reason;
    }
}
