package com.develokit.maeum_ieum.domain.report.indicator;

import lombok.Getter;

//활동 수준 지표
@Getter
public enum ActivityLevelIndicator implements ReportIndicator {
    VERY_POOR("정말 별로예요"),
    POOR("별로예요"),
    FAIR("그냥 그래요"),
    GOOD("좋아요"),
    EXCELLENT("아주 좋아요");


    private final String description;

    ActivityLevelIndicator(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String getFieldName() {
        return "activityLevelIndicator";
    }
}
