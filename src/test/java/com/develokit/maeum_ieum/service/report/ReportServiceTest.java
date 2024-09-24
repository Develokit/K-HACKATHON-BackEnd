package com.develokit.maeum_ieum.service.report;

import com.develokit.maeum_ieum.domain.report.Report;
import com.develokit.maeum_ieum.domain.report.ReportRepository;
import com.develokit.maeum_ieum.domain.user.caregiver.Caregiver;
import com.develokit.maeum_ieum.domain.user.elderly.Elderly;
import com.develokit.maeum_ieum.domain.user.elderly.ElderlyRepository;
import com.develokit.maeum_ieum.dummy.DummyObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.Optional;

import static com.develokit.maeum_ieum.dto.report.RespDto.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReportServiceTest extends DummyObject {

    @InjectMocks
    private ReportService reportService;

    @Mock
    private ReportRepository reportRepository;

    @Mock
    private ElderlyRepository elderlyRepository;

    @Test
    void 주간_보고서_정량적_평가_조회() throws JsonProcessingException {

        //given
        Caregiver caregiver = newCaregiver();
        Long elderlyId = 1L;
        Long reportId = 1L;
        Elderly elderly = newElderly(caregiver, elderlyId);
        Report report = newMockWeeklyReport(elderly, reportId);


        //when
        when(elderlyRepository.findById(elderlyId)).thenReturn(Optional.of(elderly));
        when(reportRepository.findById(any())).thenReturn(Optional.of(report));

        WeeklyReportAnalysisRespDto result = reportService.getWeeklyReportQuantitativeAnalysis(elderlyId, report.getId());

        String response = new ObjectMapper().writeValueAsString(result);
        System.out.println("response = " + response);


        //then
        assertNotNull(result);
        assertEquals(report.getMemo(), result.getMemo());
        assertEquals(report.getHealthStatusIndicator().getDescription(), result.getHealthStatus());
        assertEquals(report.getActivityLevelIndicator().getDescription(), result.getActivityLevel());
        assertEquals(report.getCognitiveFunctionIndicator().getDescription(), result.getCognitiveFunction());
        assertEquals(report.getLifeSatisfactionIndicator().getDescription(), result.getLifeSatisfaction());
        assertEquals(report.getPsychologicalStabilityIndicator().getDescription(), result.getPsychologicalStability());
        assertEquals(report.getSocialConnectivityIndicator().getDescription(), result.getSocialConnectivity());
        assertEquals(report.getSupportNeedsIndicator().getDescription(), result.getSupportNeeds());

        //정량적 분석 검증
        Map<String, String> quantitativeAnalysis = report.getParsedQuantitativeAnalysis();
        assertNotNull(quantitativeAnalysis);

        assertTrue(quantitativeAnalysis.containsKey("healthStatusIndicator"));
        assertEquals("유우시쿤 건강상태 초 사이코🤍", quantitativeAnalysis.get("healthStatusIndicator"));

        assertTrue(quantitativeAnalysis.containsKey("activityLevelIndicator"));
        assertEquals("유우시쿤 활동량 초 타카이🤍 일일 평균 걸음 수: 15,000보", quantitativeAnalysis.get("activityLevelIndicator"));

        assertTrue(quantitativeAnalysis.containsKey("cognitiveFunctionIndicator"));
        assertEquals("인지 기능 테스트 점수: 25/30, 일상생활 수행 능력 양호", quantitativeAnalysis.get("cognitiveFunctionIndicator"));

        assertTrue(quantitativeAnalysis.containsKey("lifeSatisfactionIndicator"));
        assertEquals("주관적 행복도 점수: 4/10, 유우시쿤 개선이 필요행ㅠㅠ!!", quantitativeAnalysis.get("lifeSatisfactionIndicator"));

        assertTrue(quantitativeAnalysis.containsKey("psychologicalStabilityIndicator"));
        assertEquals("우울증 선별 검사 점수: 15/20, 전문가 상담 권장", quantitativeAnalysis.get("psychologicalStabilityIndicator"));

        assertTrue(quantitativeAnalysis.containsKey("socialConnectivityIndicator"));
        assertEquals("주간 사회활동 참여 횟수: 4회, 사회적 관계 만족도 높음", quantitativeAnalysis.get("socialConnectivityIndicator"));

        assertTrue(quantitativeAnalysis.containsKey("supportNeedsIndicator"));
        assertEquals("일상생활 지원 필요도: 중간, 유우시군 주 2회 방문 요양 서비스 권장🤍", quantitativeAnalysis.get("supportNeedsIndicator"));

    }

    @Test
    void createReportMemo() {
    }

    @Test
    void getElderlyWeeklyReportList() {
    }

    @Test
    void getElderlyMonthlyReportList() {
    }

    @Test
    void createWeeklyEmptyReports() {
    }

    @Test
    void createMonthlyEmptyReports() {
    }
}