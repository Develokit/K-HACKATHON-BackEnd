package com.develokit.maeum_ieum.dto.report;

import com.develokit.maeum_ieum.domain.report.Report;
import com.develokit.maeum_ieum.domain.user.elderly.Elderly;
import com.develokit.maeum_ieum.util.CustomUtil;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class RespDto {


    @Getter
    @NoArgsConstructor
    @Schema(description = "보고서 정량적 평가 지표")
    @ToString
    public static class QuantitativeAnalysis {

        @JsonProperty("healthStatusIndicator")
        @Schema(description = "건강 상태 지표 정량적 분석 결과")
        private String healthStatusIndicator;

        @JsonProperty("activityLevelIndicator")
        @Schema(description = "활동 수준 지표 정량적 분석 결과")
        private String activityLevelIndicator;

        @JsonProperty("cognitiveFunctionIndicator")
        @Schema(description = "인지 기능 지표 정량적 분석 결과")
        private String cognitiveFunctionIndicator;

        @JsonProperty("lifeSatisfactionIndicator")
        @Schema(description = "생활 만족도 지표 정량적 분석 결과")
        private String lifeSatisfactionIndicator;

        @JsonProperty("psychologicalStabilityIndicator")
        @Schema(description = "심리적 안정 지표 정량적 분석 결과")
        private String psychologicalStabilityIndicator;

        @JsonProperty("socialConnectivityIndicator")
        @Schema(description = "사회적 연결성 수준 지표 정량적 분석 결과")
        private String socialConnectivityIndicator;

        @JsonProperty("supportNeedsIndicator")
        @Schema(description = "필요 지원 지표 정량적 분석 결과")
        private String supportNeedsIndicator;
    }
    @NoArgsConstructor
    @Getter
    @Schema(description = "월간 보고서 정량적 & 정성적 평가 조회 반환 DTO")
    public static class MonthlyReportAnalysisRespDto {
        private static final Gson gson = new Gson();
        private static final Logger log = LoggerFactory.getLogger(MonthlyReportAnalysisRespDto.class);

        @Schema(description = "정성적 분석 결과")
        private String qualitativeAnalysis;

        @Schema(description = "노인 이름")
        private String elderlyName;

        @Schema(description = "월간 보고서 분석 시작일", example =  "2024.09")
        private String startDate;

        @Schema(description = "보고서 메모")
        private String memo;

        @Schema(description = "건강 상태 지표", example =  "그저 그래요")
        private String healthStatus;

        @Schema(description = "활동 수준 지표", example =  "그저 그래요")
        private String activityLevel;

        @Schema(description = "인지 기능 지표", example =  "그저 그래요")
        private String cognitiveFunction;

        @Schema(description = "생활 만족도 지표", example =  "그저 그래요")
        private String lifeSatisfaction;

        @Schema(description = "심리적 안정 지표", example =  "그저 그래요")
        private String psychologicalStability;

        @Schema(description = "사회적 연결성 지표", example =  "그저 그래요")
        private String socialConnectivity;

        @Schema(description = "필요 지원 지표", example =  "그저 그래요")
        private String supportNeeds;

        @Schema(description = "정량적 분석 결과")
        private QuantitativeAnalysis quantitativeAnalysis;

        public MonthlyReportAnalysisRespDto(Report report, Elderly elderly){
            this.elderlyName = elderly.getName();
            this.qualitativeAnalysis = report.getQualitativeAnalysis();
            this.startDate = CustomUtil.LocalDateToMonthlyReportPublishedDate(report.getStartDate());
            this.memo = report.getMemo();
            this.healthStatus = report.getHealthStatusIndicator().getDescription();
            this.activityLevel = report.getActivityLevelIndicator().getDescription();
            this.cognitiveFunction = report.getCognitiveFunctionIndicator().getDescription();
            this.lifeSatisfaction = report.getLifeSatisfactionIndicator().getDescription();
            this.psychologicalStability = report.getPsychologicalStabilityIndicator().getDescription();
            this.socialConnectivity = report.getSocialConnectivityIndicator().getDescription();
            this.supportNeeds = report.getSupportNeedsIndicator().getDescription();
            // JSON 문자열을 Map으로 파싱
            try {
                this.quantitativeAnalysis = gson.fromJson(report.getQuantitativeAnalysis(), QuantitativeAnalysis.class);
            }catch(JsonSyntaxException e){
                log.error("JSON 파싱 중 오류 발생: 구문 오류 ", e);
            }
        }
    }

    @NoArgsConstructor
    @Getter
    @Schema(description = "주간 보고서 정량적 & 정성적 평가 조회 반환 DTO")
    public static class WeeklyReportAnalysisRespDto {
        private static final Gson gson = new Gson();

        private static final Logger log = LoggerFactory.getLogger(WeeklyReportAnalysisRespDto.class);

        @Schema(description = "정성적 분석 결과")
        private String qualitativeAnalysis;

        @Schema(description = "노인 이름")
        private String elderlyName;

        @Schema(description = "주간 보고서 분석 시작일", example =  "2024.09.20.")
        private String startDate;
        @Schema(description = "주간 보고서 분석 종료일", example =  "2024.09.20.")
        private String endDate;

        @Schema(description = "주간 보고서 발행 요일", example =  "(금)")
        private String reportDay; //DayOfWeek 타입의 reportDay를 한글로 변환시킨 것

        @Schema(description = "보고서 메모")
        private String memo;

        @Schema(description = "건강 상태 지표", example =  "그저 그래요")
        private String healthStatus;

        @Schema(description = "활동 수준 지표", example =  "그저 그래요")
        private String activityLevel;

        @Schema(description = "인지 기능 지표", example =  "그저 그래요")
        private String cognitiveFunction;

        @Schema(description = "생활 만족도 지표", example =  "그저 그래요")
        private String lifeSatisfaction;

        @Schema(description = "심리적 안정 지표", example =  "그저 그래요")
        private String psychologicalStability;

        @Schema(description = "사회적 연결성 지표", example =  "그저 그래요")
        private String socialConnectivity;

        @Schema(description = "필요 지원 지표", example =  "그저 그래요")
        private String supportNeeds;

        @Schema(description = "정량적 분석 결과")
        private QuantitativeAnalysis quantitativeAnalysis;



        public WeeklyReportAnalysisRespDto(Report report, Elderly elderly){
            this.elderlyName = elderly.getName();
            this.qualitativeAnalysis = report.getQualitativeAnalysis();
            this.reportDay = report.getReportDay()==null?null:CustomUtil.DayOfWeekToString(report.getReportDay()); //(수)
            this.startDate = CustomUtil.LocalDateToWeeklyReportCreatedDate(report.getStartDate());
            this.endDate = CustomUtil.LocalDateTimeToWeeklyReportPublishedDate(report.getEndDate(), 1); //일자를 하나 차감해야 함
            this.memo = report.getMemo();
            this.healthStatus = report.getHealthStatusIndicator().getDescription();
            this.activityLevel = report.getActivityLevelIndicator().getDescription();
            this.cognitiveFunction = report.getCognitiveFunctionIndicator().getDescription();
            this.lifeSatisfaction = report.getLifeSatisfactionIndicator().getDescription();
            this.psychologicalStability = report.getPsychologicalStabilityIndicator().getDescription();
            this.socialConnectivity = report.getSocialConnectivityIndicator().getDescription();
            this.supportNeeds = report.getSupportNeedsIndicator().getDescription();
                // JSON 문자열을 Map으로 파싱
            try {
                this.quantitativeAnalysis = gson.fromJson(report.getQuantitativeAnalysis(), QuantitativeAnalysis.class);
            }catch(JsonSyntaxException e){
                log.error("JSON 파싱 중 오류 발생: 구문 오류 ", e);
            }

        }
    }

    @NoArgsConstructor
    @Getter
    @Schema(description = "보고서 메모 생성 반환 DTO")
    public static class ReportMemoCreateRespDto{
        public ReportMemoCreateRespDto(Report report) {
            this.memo = report.getMemo();
        }
        @Schema(description = "작성한 메모")
        private String memo;
    }
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
                this.startDate = CustomUtil.LocalDateToMonthlyReportPublishedDate(report.getStartDate()); //2024.07
            }

            @Schema(description = "월간 보고서 아이디")
            private Long reportId; //보고서 아이디
            @Schema(description = "월간 보고서 생성 날짜", example =  "2024.09")
            private String startDate; //발행 날짜
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
                this.startDate = CustomUtil.LocalDateToWeeklyReportCreatedDate(report.getStartDate()); //2024.07.07.
                this.reportDay = report.getReportDay()==null?null:CustomUtil.DayOfWeekToString(report.getReportDay()); //(수)
            }

            @Schema(description = "주간 보고서 아이디")
            private Long reportId; //보고서 아이디
            @Schema(description = "주간 보고서 생성 날짜", example =  "2024.09.20.")
            private String startDate; //발행 날짜
            @Schema(description = "주간 보고서 발행 요일", example =  "(금)")
            private String reportDay; //DayOfWeek 타입의 reportDay를 한글로 변환시킨 것
        }
    }
}
