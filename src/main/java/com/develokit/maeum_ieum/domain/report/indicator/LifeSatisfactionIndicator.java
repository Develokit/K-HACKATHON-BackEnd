package com.develokit.maeum_ieum.domain.report.indicator;

import lombok.Getter;

//생활 만족도 지표
@Getter
public enum LifeSatisfactionIndicator {
    VERY_POOR("정말 별로예요"),
    POOR("별로예요"),
    FAIR("그냥 그래요"),
    GOOD("좋아요"),
    EXCELLENT("아주 좋아요");

    private final String description;

    LifeSatisfactionIndicator(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
