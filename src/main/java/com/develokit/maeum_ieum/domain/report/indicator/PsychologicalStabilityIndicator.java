package com.develokit.maeum_ieum.domain.report.indicator;

import lombok.Getter;

@Getter
//심리적 안정 지표

public enum PsychologicalStabilityIndicator {
    VERY_POOR("정말 별로예요"),
    POOR("별로예요"),
    FAIR("그냥 그래요"),
    GOOD("좋아요"),
    EXCELLENT("아주 좋아요");

    private final String description;

    PsychologicalStabilityIndicator(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
