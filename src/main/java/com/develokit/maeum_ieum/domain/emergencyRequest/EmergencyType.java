package com.develokit.maeum_ieum.domain.emergencyRequest;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum EmergencyType {
    CAREGIVER_NOTIFY("긴급 알림");

    private final String symbol;

    public static EmergencyType fromString(String value) {
        try {
            return EmergencyType.valueOf(value);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("지원하지 않는 EmergencyType: " + value);
        }
    }


}
