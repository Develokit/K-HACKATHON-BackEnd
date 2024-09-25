package com.develokit.maeum_ieum.service.report;

import com.develokit.maeum_ieum.domain.message.Message;
import com.develokit.maeum_ieum.domain.report.Report;
import com.develokit.maeum_ieum.domain.report.ReportRepository;
import com.develokit.maeum_ieum.domain.report.ReportType;
import com.develokit.maeum_ieum.domain.report.indicator.*;
import com.develokit.maeum_ieum.domain.user.caregiver.Caregiver;
import com.develokit.maeum_ieum.domain.user.elderly.Elderly;
import com.develokit.maeum_ieum.dto.openAi.run.ReqDto;
import com.develokit.maeum_ieum.dummy.DummyObject;
import com.develokit.maeum_ieum.ex.CustomApiException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static com.develokit.maeum_ieum.dto.openAi.run.ReqDto.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


class WeeklyReportAnalysisServiceTest extends DummyObject {
    @Mock
    private WebClient webClient;

    @Mock
    private ReportRepository reportRepository;

    @InjectMocks
    private WeeklyReportAnalysisService weeklyReportAnalysisService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

//    @Test
//    void generateWeeklyReportAnalysis_Success() {
//        // Given
//        Caregiver caregiver = newCaregiver();
//        Elderly elderly = newElderly(caregiver, 1L);
//        Report report = emptyReport(elderly, ReportType.WEEKLY);
//        List<Message> messageList = new ArrayList<>();
//        messageList.add(newMockAIMessage(elderly));
//        messageList.add(newMockUserMessage(elderly));
//
//
//        CreateRunReqDto createRunReqDto = new CreateRunReqDto("assistantId", true);
//        String expectedAnalysisResult = "**ActivityLevelIndicator: GOOD**\n**이유:** 활동량이 적절함\n" +
//                "**HealthStatusIndicator: GOOD**\n**이유:** 건강 상태가 양호함\n" +
//                "**CognitiveFunctionIndicator: GOOD**\n**이유:** 인지 기능이 정상적임\n" +
//                "**LifeSatisfactionIndicator: GOOD**\n**이유:** 삶의 만족도가 높음\n" +
//                "**PsychologicalStabilityIndicator: GOOD**\n**이유:** 심리적으로 안정적임\n" +
//                "**SocialConnectivityIndicator: GOOD**\n**이유:** 사회적 연결성이 좋음\n" +
//                "**SupportNeedsIndicator: GOOD**\n**이유:** 지원 필요성이 낮음";
//
//        WebClient.RequestBodySpec requestBodySpec = mock(WebClient.RequestBodySpec.class);
//        WebClient.RequestHeadersSpec<?> requestHeadersSpec = mock(WebClient.RequestHeadersSpec.class);
//        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);
//
//        when(webClient.post()).thenReturn((WebClient.RequestBodyUriSpec) requestBodySpec);
//        when(requestBodySpec.wait(any(String.class), any())).thenReturn(requestBodySpec);
//        when(requestBodySpec.bodyValue(any())).thenReturn(requestHeadersSpec);
//        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
//        when(responseSpec.bodyToFlux((Class<Object>) any())).thenReturn(Mono.just(new ServerSentEvent<>(expectedAnalysisResult)));
//
//        when(reportRepository.save(any(Report.class))).thenReturn(Mono.just(report));
//
//        // When
//        Mono<Report> resultMono = weeklyReportAnalysisService.generateWeeklyReportAnalysis(report, messageList);
//
//        // Then
//        Report result = resultMono.block();
//        assertNotNull(result);
//        verify(reportRepository, times(1)).save(report);
//    }



}