package com.develokit.maeum_ieum.service;

import com.develokit.maeum_ieum.domain.assistant.Assistant;
import com.develokit.maeum_ieum.domain.assistant.AssistantRepository;
import com.develokit.maeum_ieum.domain.emergencyRequest.EmergencyRequest;
import com.develokit.maeum_ieum.domain.emergencyRequest.EmergencyRequestRepository;
import com.develokit.maeum_ieum.domain.message.Message;
import com.develokit.maeum_ieum.domain.message.MessageRepository;
import com.develokit.maeum_ieum.domain.report.Report;
import com.develokit.maeum_ieum.domain.report.ReportRepository;
import com.develokit.maeum_ieum.domain.user.caregiver.CareGiverRepository;
import com.develokit.maeum_ieum.domain.user.caregiver.Caregiver;
import com.develokit.maeum_ieum.domain.user.elderly.Elderly;
import com.develokit.maeum_ieum.domain.user.elderly.ElderlyRepository;
import com.develokit.maeum_ieum.dto.elderly.RespDto;
import com.develokit.maeum_ieum.dummy.DummyObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.develokit.maeum_ieum.dto.elderly.RespDto.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) //Mokito 테스트
class ElderlyServiceTest extends DummyObject {

    @InjectMocks
    private ElderlyService elderlyService;

    @Mock
    private ElderlyRepository elderlyRepository;

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private ReportRepository reportRepository;

    @Mock
    private EmergencyRequestRepository emergencyRequestRepository;

    @Mock
    private AssistantRepository assistantRepository;

    @Mock
    private CareGiverRepository careGiverRepository;
    @Mock
    private S3Service s3Service;


    @Test
    void 노인삭제기능_이미지없는경우_테스트() throws Exception{
        //given
        Long elderlyId = 1L;
        Caregiver caregiver = newCaregiver();
        Elderly elderly = newElderly(caregiver, elderlyId);
        Assistant assistant = newMockAssistant(caregiver);
        elderly.attachAssistant(assistant);

        Message aiMessage = newMockAIMessage(elderly);
        Message userMessage = newMockUserMessage(elderly);

        EmergencyRequest emergencyRequest = newMockEmergencyRequest(elderly, caregiver);

        Report weeklyReport = newMockWeeklyReport(elderly);
        Report monthlyReport = newMockMonthlyReport(elderly);

        List<Report> reports = Arrays.asList(weeklyReport, monthlyReport);
        List<EmergencyRequest> emergencyRequests = Collections.singletonList(emergencyRequest);


        //when
        when(careGiverRepository.findByUsername(caregiver.getUsername())).thenReturn(Optional.of(caregiver));
        when(elderlyRepository.findById(any())).thenReturn(Optional.of(elderly));
        when(reportRepository.findByElderly(elderly)).thenReturn(reports);
        when(messageRepository.deleteAllByElderly(elderly)).thenReturn(2);
        when(emergencyRequestRepository.findByElderly(elderly)).thenReturn(emergencyRequests);

        // execute
        ElderlyDeleteRespDto result = elderlyService.deleteElderly(elderlyId, caregiver.getUsername());


        //then
        verify(s3Service, never()).deleteImage(anyString());
        verify(messageRepository).deleteAllByElderly(elderly);
        verify(careGiverRepository).findByUsername(caregiver.getUsername());
        verify(elderlyRepository).findById(elderlyId);
        verify(reportRepository).findByElderly(elderly);
        verify(emergencyRequestRepository).findByElderly(elderly);

        assertThat(result).isNotNull();
        assertThat(result.getElderlyName()).isEqualTo(elderly.getName());
        assertThat(!caregiver.getElderlyList().contains(elderly)).isTrue();
        assertThat(!caregiver.getAssistantList().contains(assistant)).isTrue();
        assertThat(!caregiver.getEmergencyRequestList().contains(emergencyRequests.get(0))).isTrue();
        assertThat(elderly.getWeeklyReports().contains(weeklyReport)).isFalse();
        assertThat(elderly.getMonthlyReports().contains(monthlyReport)).isFalse();

    }
    @Test
    void createElderlyTest() {

    }
}