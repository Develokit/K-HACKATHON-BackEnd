package com.develokit.maeum_ieum.domain.report.indicator;

//필요 지원 지표
public enum SupportNeedsIndicator implements ReportIndicator {
    VERY_POOR("아주 필요해요"),
    POOR("필요해요"),
    FAIR("보통이예요"),
    GOOD("필요없어요"),
    EXCELLENT("정말 필요없어요");

    private final String description;

    SupportNeedsIndicator(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String getFieldName() {
        return "supportNeedsIndicator";
    }
}
