package com.develokit.maeum_ieum.dummy;

import com.develokit.maeum_ieum.domain.assistant.Assistant;
import com.develokit.maeum_ieum.domain.emergencyRequest.EmergencyRequest;
import com.develokit.maeum_ieum.domain.emergencyRequest.EmergencyType;
import com.develokit.maeum_ieum.domain.message.Message;
import com.develokit.maeum_ieum.domain.message.MessageType;
import com.develokit.maeum_ieum.domain.report.Report;
import com.develokit.maeum_ieum.domain.report.ReportStatus;
import com.develokit.maeum_ieum.domain.report.ReportType;
import com.develokit.maeum_ieum.domain.report.indicator.*;
import com.develokit.maeum_ieum.domain.user.Gender;
import com.develokit.maeum_ieum.domain.user.Role;
import com.develokit.maeum_ieum.domain.user.caregiver.Caregiver;
import com.develokit.maeum_ieum.domain.user.elderly.Elderly;
import com.develokit.maeum_ieum.domain.user.elderly.EmergencyContactInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DummyObject {

    protected EmergencyRequest mockEmergencyRequest(Elderly elderly, Caregiver caregiver){
        return EmergencyRequest.builder().elderly(elderly).caregiver(caregiver).emergencyType(EmergencyType.CAREGIVER_NOTIFY).build();
    }

    protected Report mockMonthlyReport(Elderly elderly) throws JsonProcessingException {

        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = endDate.minusMonths(1);

        Report report = Report.builder()
                .reportType(ReportType.MONTHLY)
                .elderly(elderly)
                .startDate(startDate)
                .endDate(endDate)
                .healthStatusIndicator(HealthStatusIndicator.EXCELLENT)
                .activityLevelIndicator(ActivityLevelIndicator.EXCELLENT)
                .cognitiveFunctionIndicator(CognitiveFunctionIndicator.FAIR)
                .lifeSatisfactionIndicator(LifeSatisfactionIndicator.POOR)
                .psychologicalStabilityIndicator(PsychologicalStabilityIndicator.VERY_POOR)
                .socialConnectivityIndicator(SocialConnectivityIndicator.GOOD)
                .supportNeedsIndicator(SupportNeedsIndicator.FAIR)
                .reportStatus(ReportStatus.COMPLETED)
                .memo("유우시군을 분석했어🤍")
                .build();

        report.setQuantitativeAnalysis(HealthStatusIndicator.EXCELLENT, "유우시쿤 건강상태 초 사이코🤍");
        report.setQuantitativeAnalysis(ActivityLevelIndicator.EXCELLENT, "유우시쿤 활동량 초 타카이🤍 일일 평균 걸음 수: 15,000보");
        report.setQuantitativeAnalysis(CognitiveFunctionIndicator.FAIR, "인지 기능 테스트 점수: 25/30, 일상생활 수행 능력 양호");
        report.setQuantitativeAnalysis(LifeSatisfactionIndicator.POOR, "주관적 행복도 점수: 4/10, 유우시쿤 개선이 필요행ㅠㅠ!!");
        report.setQuantitativeAnalysis(PsychologicalStabilityIndicator.VERY_POOR, "우울증 선별 검사 점수: 15/20, 전문가 상담 권장");
        report.setQuantitativeAnalysis(SocialConnectivityIndicator.GOOD, "주간 사회활동 참여 횟수: 4회, 사회적 관계 만족도 높음");
        report.setQuantitativeAnalysis(SupportNeedsIndicator.FAIR, "일상생활 지원 필요도: 중간, 유우시군 주 2회 방문 요양 서비스 권장🤍");
        return report;
    }

    protected Report mockWeeklyReport(Elderly elderly) throws JsonProcessingException {

        LocalDateTime endDate= LocalDateTime.now();
        LocalDateTime startDate = endDate.minusWeeks(1);

        Report report = Report.builder()
                .reportType(ReportType.WEEKLY)
                .elderly(elderly)
                .startDate(startDate)
                .endDate(endDate)
                .healthStatusIndicator(HealthStatusIndicator.EXCELLENT)
                .activityLevelIndicator(ActivityLevelIndicator.EXCELLENT)
                .cognitiveFunctionIndicator(CognitiveFunctionIndicator.FAIR)
                .lifeSatisfactionIndicator(LifeSatisfactionIndicator.POOR)
                .psychologicalStabilityIndicator(PsychologicalStabilityIndicator.VERY_POOR)
                .socialConnectivityIndicator(SocialConnectivityIndicator.GOOD)
                .supportNeedsIndicator(SupportNeedsIndicator.FAIR)
                .reportStatus(ReportStatus.COMPLETED)
                .memo("유우시군을 분석했어🤍")
                .build();

        report.setQuantitativeAnalysis(HealthStatusIndicator.EXCELLENT, "유우시쿤 건강상태 초 사이코🤍");
        report.setQuantitativeAnalysis(ActivityLevelIndicator.EXCELLENT, "유우시쿤 활동량 초 타카이🤍 일일 평균 걸음 수: 15,000보");
        report.setQuantitativeAnalysis(CognitiveFunctionIndicator.FAIR, "인지 기능 테스트 점수: 25/30, 일상생활 수행 능력 양호");
        report.setQuantitativeAnalysis(LifeSatisfactionIndicator.POOR, "주관적 행복도 점수: 4/10, 유우시쿤 개선이 필요행ㅠㅠ!!");
        report.setQuantitativeAnalysis(PsychologicalStabilityIndicator.VERY_POOR, "우울증 선별 검사 점수: 15/20, 전문가 상담 권장");
        report.setQuantitativeAnalysis(SocialConnectivityIndicator.GOOD, "주간 사회활동 참여 횟수: 4회, 사회적 관계 만족도 높음");
        report.setQuantitativeAnalysis(SupportNeedsIndicator.FAIR, "일상생활 지원 필요도: 중간, 유우시군 주 2회 방문 요양 서비스 권장🤍");
        return report;
    }


    protected Report newMockWeeklyReport(Elderly elderly, Long reportId) throws JsonProcessingException {
        Report report = Report.builder()
                .id(reportId)
                .reportType(ReportType.MONTHLY)
                .elderly(elderly)
                .healthStatusIndicator(HealthStatusIndicator.EXCELLENT)
                .activityLevelIndicator(ActivityLevelIndicator.EXCELLENT)
                .cognitiveFunctionIndicator(CognitiveFunctionIndicator.FAIR)
                .lifeSatisfactionIndicator(LifeSatisfactionIndicator.POOR)
                .psychologicalStabilityIndicator(PsychologicalStabilityIndicator.VERY_POOR)
                .socialConnectivityIndicator(SocialConnectivityIndicator.GOOD)
                .supportNeedsIndicator(SupportNeedsIndicator.FAIR)
                .reportStatus(ReportStatus.COMPLETED)
                .memo("유우시군을 분석했어🤍")
                .build();

        report.setQuantitativeAnalysis(HealthStatusIndicator.EXCELLENT, "유우시쿤 건강상태 초 사이코🤍");
        report.setQuantitativeAnalysis(ActivityLevelIndicator.EXCELLENT, "유우시쿤 활동량 초 타카이🤍 일일 평균 걸음 수: 15,000보");
        report.setQuantitativeAnalysis(CognitiveFunctionIndicator.FAIR, "인지 기능 테스트 점수: 25/30, 일상생활 수행 능력 양호");
        report.setQuantitativeAnalysis(LifeSatisfactionIndicator.POOR, "주관적 행복도 점수: 4/10, 유우시쿤 개선이 필요행ㅠㅠ!!");
        report.setQuantitativeAnalysis(PsychologicalStabilityIndicator.VERY_POOR, "우울증 선별 검사 점수: 15/20, 전문가 상담 권장");
        report.setQuantitativeAnalysis(SocialConnectivityIndicator.GOOD, "주간 사회활동 참여 횟수: 4회, 사회적 관계 만족도 높음");
        report.setQuantitativeAnalysis(SupportNeedsIndicator.FAIR, "일상생활 지원 필요도: 중간, 유우시군 주 2회 방문 요양 서비스 권장🤍");
        return report;
    }

    protected List<Report> newMockMonthlyReportList(Elderly elderly) throws JsonProcessingException {
        List<Report> reportList = new ArrayList<>();
        for(long i = 20;i<=30;i++){
            Report report = Report.builder()
                    .id(i)
                    .reportType(ReportType.MONTHLY)
                    .healthStatusIndicator(HealthStatusIndicator.EXCELLENT)
                    .activityLevelIndicator(ActivityLevelIndicator.EXCELLENT)
                    .cognitiveFunctionIndicator(CognitiveFunctionIndicator.FAIR)
                    .lifeSatisfactionIndicator(LifeSatisfactionIndicator.POOR)
                    .psychologicalStabilityIndicator(PsychologicalStabilityIndicator.VERY_POOR)
                    .socialConnectivityIndicator(SocialConnectivityIndicator.GOOD)
                    .supportNeedsIndicator(SupportNeedsIndicator.FAIR)
                    .build();

            //정량적 분석 결과 설정
            report.setQuantitativeAnalysis(HealthStatusIndicator.EXCELLENT, "유우시쿤 건강상태 초 사이코🤍");
            report.setQuantitativeAnalysis(ActivityLevelIndicator.EXCELLENT, "유우시쿤 활동량 초 타카이🤍 일일 평균 걸음 수: 15,000보");
            report.setQuantitativeAnalysis(CognitiveFunctionIndicator.FAIR, "인지 기능 테스트 점수: 25/30, 일상생활 수행 능력 양호");
            report.setQuantitativeAnalysis(LifeSatisfactionIndicator.POOR, "주관적 행복도 점수: 4/10, 유우시쿤 개선이 필요행ㅠㅠ!!");
            report.setQuantitativeAnalysis(PsychologicalStabilityIndicator.VERY_POOR, "우울증 선별 검사 점수: 15/20, 전문가 상담 권장");
            report.setQuantitativeAnalysis(SocialConnectivityIndicator.GOOD, "주간 사회활동 참여 횟수: 4회, 사회적 관계 만족도 높음");
            report.setQuantitativeAnalysis(SupportNeedsIndicator.FAIR, "일상생활 지원 필요도: 중간, 유우시군 주 2회 방문 요양 서비스 권장🤍");

            reportList.add(report);
        }
        return reportList;
    }

    protected List<Report> newMockWeeklyReportList(Elderly elderly) throws JsonProcessingException {
        List<Report> reportList = new ArrayList<>();
        for(long i = 1;i<=10;i++){
            Report report = Report.builder()
                    .id(i)
                    .reportType(ReportType.WEEKLY)
                    .healthStatusIndicator(HealthStatusIndicator.EXCELLENT)
                    .activityLevelIndicator(ActivityLevelIndicator.EXCELLENT)
                    .cognitiveFunctionIndicator(CognitiveFunctionIndicator.FAIR)
                    .lifeSatisfactionIndicator(LifeSatisfactionIndicator.POOR)
                    .psychologicalStabilityIndicator(PsychologicalStabilityIndicator.VERY_POOR)
                    .socialConnectivityIndicator(SocialConnectivityIndicator.GOOD)
                    .supportNeedsIndicator(SupportNeedsIndicator.FAIR)
                    .build();

            //정량적 분석 결과 설정
            report.setQuantitativeAnalysis(HealthStatusIndicator.EXCELLENT, "유우시쿤 건강상태 초 사이코🤍");
            report.setQuantitativeAnalysis(ActivityLevelIndicator.EXCELLENT, "유우시쿤 활동량 초 타카이🤍 일일 평균 걸음 수: 15,000보");
            report.setQuantitativeAnalysis(CognitiveFunctionIndicator.FAIR, "인지 기능 테스트 점수: 25/30, 일상생활 수행 능력 양호");
            report.setQuantitativeAnalysis(LifeSatisfactionIndicator.POOR, "주관적 행복도 점수: 4/10, 유우시쿤 개선이 필요행ㅠㅠ!!");
            report.setQuantitativeAnalysis(PsychologicalStabilityIndicator.VERY_POOR, "우울증 선별 검사 점수: 15/20, 전문가 상담 권장");
            report.setQuantitativeAnalysis(SocialConnectivityIndicator.GOOD, "주간 사회활동 참여 횟수: 4회, 사회적 관계 만족도 높음");
            report.setQuantitativeAnalysis(SupportNeedsIndicator.FAIR, "일상생활 지원 필요도: 중간, 유우시군 주 2회 방문 요양 서비스 권장🤍");

            reportList.add(report);
        }
        return reportList;
    }

    protected Assistant newMockAssistant(Caregiver caregiver){
        return Assistant.builder()
                .caregiver(caregiver)
                .name("mock Assistant")
                .id(1L)
                .build();
    }

    protected EmergencyRequest newMockEmergencyRequest(Elderly elderly, Caregiver caregiver){
        return EmergencyRequest.builder()
                .caregiver(caregiver)
                .elderly(elderly)
                .emergencyType(EmergencyType.CAREGIVER_NOTIFY)
                .build();
    }

    protected Message newMockUserMessage(Elderly elderly){
        return Message.builder()
                .content("User Message")
                .id(1L)
                .messageType(MessageType.USER)
                .elderly(elderly)
                .build();
    }

    protected Message newMockAIMessage(Elderly elderly){
        return Message.builder()
                .content("User Message")
                .id(2L)
                .messageType(MessageType.AI)
                .elderly(elderly)
                .build();
    }


    protected Report newMockMonthlyReport(Elderly elderly){
        return Report.builder()
                .elderly(elderly)
                .id(2L)
                .reportType(ReportType.MONTHLY)
                .build();
    }

    protected Caregiver newCaregiver(){
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        return Caregiver.builder()
                .name("userA1111")
                .password(bCryptPasswordEncoder.encode("userA1111"))
                .username("userA1234")
                .gender(Gender.FEMALE)
                .contact("010-1234-5678")
                .organization("jeju")
                .role(Role.ADMIN)
                .birthDate(LocalDate.now())
                .build();
    }
    protected Elderly newElderly(Caregiver caregiver,Long elderlyId){
        return Elderly.builder()
                .name("노인1")
                .role(Role.USER)
                .contact("010-1111-3333")
                .healthInfo("탕후루")
                .birthDate(LocalDate.now())
                .imgUrl(null)
                .gender(Gender.FEMALE)
                .homeAddress("제주시 노형동")
                .caregiver(caregiver)
                .emergencyContactInfo(new EmergencyContactInfo("sss", "010-3333-4444", "손녀"))
                .build();
    }
}
