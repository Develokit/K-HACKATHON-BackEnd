package com.develokit.maeum_ieum.domain.report;

import com.develokit.maeum_ieum.domain.base.BaseEntity;
import com.develokit.maeum_ieum.domain.report.indicator.*;
import com.develokit.maeum_ieum.domain.user.elderly.Elderly;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.vladmihalcea.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.core.type.TypeReference;
import org.hibernate.annotations.Type;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
public class Report extends BaseEntity {

    private static final Logger log = LoggerFactory.getLogger(Report.class);

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "elderly_id", nullable = false)
    private Elderly elderly;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReportType reportType; //월간 보고서/주간 보고서

    private LocalDate startDate; // 보고서 기록 시작 일
    private LocalDateTime endDate;// 보고서 기록 종료 일

    @Type(JsonType.class)
    @Column(columnDefinition = "json")
    private String quantitativeAnalysis; //정량적 분석 결과

    @Column(length = 2048)
    private String qualitativeAnalysis; //정성적 분석 결과
    @Column(length = 512)
    private String memo; //메모

    @Enumerated(EnumType.STRING)
    private ReportStatus reportStatus;

    @Enumerated(EnumType.STRING)
    private DayOfWeek reportDay;


    //================================보고서 지표================================

    @Enumerated(EnumType.STRING)
    private HealthStatusIndicator healthStatusIndicator; //건강 상태 지표

    @Enumerated(EnumType.STRING)
    private ActivityLevelIndicator activityLevelIndicator; //활동 수준 지표

    @Enumerated(EnumType.STRING)
    private CognitiveFunctionIndicator cognitiveFunctionIndicator; //인지 기능 지표

    @Enumerated(EnumType.STRING)
    private LifeSatisfactionIndicator lifeSatisfactionIndicator;//생활 만족도 지표

    @Enumerated(EnumType.STRING)
    private PsychologicalStabilityIndicator psychologicalStabilityIndicator; //심리적 안정 지표

    @Enumerated(EnumType.STRING)
    private SocialConnectivityIndicator socialConnectivityIndicator; //사회적 연결성 지표

    @Enumerated(EnumType.STRING)
    private SupportNeedsIndicator supportNeedsIndicator; //필요 지원 지표

    private static final ObjectMapper om = new ObjectMapper();


    @Builder
    public Report(Long id, Elderly elderly, ReportType reportType, LocalDate startDate, LocalDateTime endDate, String quantitativeAnalysis, String qualitativeAnalysis, String memo, ReportStatus reportStatus, DayOfWeek reportDay, HealthStatusIndicator healthStatusIndicator, ActivityLevelIndicator activityLevelIndicator, CognitiveFunctionIndicator cognitiveFunctionIndicator, LifeSatisfactionIndicator lifeSatisfactionIndicator, PsychologicalStabilityIndicator psychologicalStabilityIndicator, SocialConnectivityIndicator socialConnectivityIndicator, SupportNeedsIndicator supportNeedsIndicator) {
        this.id = id;
        this.elderly = elderly;
        this.reportType = reportType;
        this.startDate = startDate;
        this.endDate = endDate;
        this.quantitativeAnalysis = quantitativeAnalysis;
        this.qualitativeAnalysis = qualitativeAnalysis;
        this.memo = memo;
        this.reportStatus = reportStatus;
        this.reportDay = reportDay;
        this.healthStatusIndicator = healthStatusIndicator;
        this.activityLevelIndicator = activityLevelIndicator;
        this.cognitiveFunctionIndicator = cognitiveFunctionIndicator;
        this.lifeSatisfactionIndicator = lifeSatisfactionIndicator;
        this.psychologicalStabilityIndicator = psychologicalStabilityIndicator;
        this.socialConnectivityIndicator = socialConnectivityIndicator;
        this.supportNeedsIndicator = supportNeedsIndicator;
    }


    public <T extends Enum<T> & ReportIndicator> void setQuantitativeAnalysis(T indicator, String analysis) throws JsonProcessingException {

        // 현재 저장된 분석 결과
        ObjectNode jsonNode;
        try {
            if (this.quantitativeAnalysis == null || this.quantitativeAnalysis.isEmpty()) {
                jsonNode = om.createObjectNode();
            } else {
                jsonNode = (ObjectNode) om.readTree(this.quantitativeAnalysis);
            }
            //새로운 분석 결과 추가
            jsonNode.put(indicator.getFieldName(), analysis);
            //분석 결과 추가
            this.quantitativeAnalysis = om.writeValueAsString(jsonNode);
        } catch (Exception e) {
            log.error("JSON 처리 중 오류 발생", e);
            throw new IllegalStateException("정량적 분석 결과 처리 중 오류 발생", e);
        }


        //Enum 할당
        switch (indicator.getClass().getSimpleName()) {
            case "HealthStatusIndicator":
                this.healthStatusIndicator = (HealthStatusIndicator) indicator;
                break;
            case "ActivityLevelIndicator":
                this.activityLevelIndicator = (ActivityLevelIndicator) indicator;
                break;
            case "CognitiveFunctionIndicator":
                this.cognitiveFunctionIndicator = (CognitiveFunctionIndicator) indicator;
                break;
            case "LifeSatisfactionIndicator":
                this.lifeSatisfactionIndicator = (LifeSatisfactionIndicator) indicator;
                break;
            case "PsychologicalStabilityIndicator":
                this.psychologicalStabilityIndicator = (PsychologicalStabilityIndicator) indicator;
                break;
            case "SocialConnectivityIndicator":
                this.socialConnectivityIndicator = (SocialConnectivityIndicator) indicator;
                break;
            case "SupportNeedsIndicator":
                this.supportNeedsIndicator = (SupportNeedsIndicator) indicator;
                break;
        }

//        //리플렉션 사용
//        try {
//            //필드 찾기
//            Field field = this.getClass().getDeclaredField(indicator.getFieldName());
//            //필드 접근 허용
//            field.setAccessible(true);
//            //필드 값 설정
//            field.set(this, indicator);
//        } catch (NoSuchFieldException | IllegalAccessException e) {
//            throw new IllegalStateException("지표 설정 과정에서 오류 발생: " + indicator.getFieldName(), e);
//        }
    }

    public Map<String, String> getParsedQuantitativeAnalysis(){
        if (this.quantitativeAnalysis == null || this.quantitativeAnalysis.isEmpty()) {
            return new HashMap<>();
        }
        try {
            // JSON 문자열을 Map<String, Object>로 변환
            System.out.println("Parsing JSON: " + this.quantitativeAnalysis); // 디버깅용
            Map<String, String> result = om.readValue(this.quantitativeAnalysis, new TypeReference<Map<String, String>>() {});
            System.out.println("Parsed result: " + result); // 디버깅용
            return result;
        } catch (JsonProcessingException e) {
            System.err.println("Parse error: " + e.getMessage()); // 디버깅용
            throw new IllegalStateException("현재 분석 결과를 읽는 중 오류 발생", e);
        }
    }


    public void modifyStartDateAndReportDay(DayOfWeek dayOfWeek){
        this.reportDay = dayOfWeek;
    }
    public void updateReportDay(DayOfWeek reportDay){this.reportDay = reportDay;}

    public void updateReportStatus(ReportStatus reportStatus){
        this.reportStatus = reportStatus;
    }

    public void updateEndDate(LocalDateTime localDateTime){
        this.endDate = localDateTime;
    }

    public void setMemo(String memo){
        this.memo = memo;
    }

    public Report(Elderly elderly, LocalDate startDate, LocalDateTime endDate, ReportType reportType){
        this.elderly = elderly;
        this.startDate = startDate;
        this.endDate = endDate;
        this.reportType = reportType;
    }

}
