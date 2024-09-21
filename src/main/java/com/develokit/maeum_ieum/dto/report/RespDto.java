package com.develokit.maeum_ieum.dto.report;

import com.develokit.maeum_ieum.domain.report.Report;
import com.develokit.maeum_ieum.util.CustomUtil;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

public class RespDto {
    @NoArgsConstructor
    @Getter
    @Schema(description = "노인 월간 보고서 리스트 반환 DTO")
    public static class MonthlyReportListRespDto{
        @Schema(description = "다음 페이지 로드를 위한 커서")
        private Long nextCursor;
        @Schema(description = "월간 보고서 DTO 리스트")
        private List<MonthlyReportDto> reportList;
        public MonthlyReportListRespDto(List<Report> reportList, Long nextCursor) {
            this.nextCursor = nextCursor;
            this.reportList = reportList.stream()
                    .map(MonthlyReportDto::new)
                    .toList();
        }
        @NoArgsConstructor
        @Getter
        @Schema(description = "월간 보고서 DTO")
        public static class MonthlyReportDto{
            public MonthlyReportDto(Report report) {
                this.reportId = report.getId();
                this.publishedDate = CustomUtil.LocalDateTimeToMonthlyReportPublishedDate(report.getEndDate()); //2024.07.07.
            }

            @Schema(description = "월간 보고서 아이디")
            private Long reportId; //보고서 아이디
            @Schema(description = "월간 보고서 발행 날짜", example =  "2024.09")
            private String publishedDate; //발행 날짜
        }
    }
    @NoArgsConstructor
    @Getter
    @Schema(description = "노인 주간 보고서 리스트 반환 DTO")
    public static class WeeklyReportListRespDto {
        @Schema(description = "다음 페이지 로드를 위한 커서")
        private Long nextCursor;
        @Schema(description = "주간 보고서 DTO 리스트")
        private List<WeeklyReportDto> reportList;
        public WeeklyReportListRespDto(List<Report> reportList, Long nextCursor) {
            this.nextCursor = nextCursor;
            this.reportList = reportList.stream()
                    .map(WeeklyReportDto::new)
                    .toList();
        }

        @NoArgsConstructor
        @Getter
        @Schema(description = "주간 보고서 DTO")
        public static class WeeklyReportDto{
            public WeeklyReportDto(Report report) {
                this.reportId = report.getId();
                this.publishedDate = CustomUtil.LocalDateTimeToWeeklyReportPublishedDate(report.getEndDate()); //2024.07.07.
                this.reportDay = report.getReportDay()==null?null:CustomUtil.DayOfWeekToString(report.getReportDay()); //(수)
            }

            @Schema(description = "주간 보고서 아이디")
            private Long reportId; //보고서 아이디
            @Schema(description = "주간 보고서 발행 날짜", example =  "2024.09.20.")
            private String publishedDate; //발행 날짜
            @Schema(description = "주간 보고서 발행 요일", example =  "(금)")
            private String reportDay; //DayOfWeek 타입의 reportDay를 한글로 변환시킨 것
        }
    }
}
