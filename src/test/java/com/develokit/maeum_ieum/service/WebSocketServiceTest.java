package com.develokit.maeum_ieum.service;

import com.develokit.maeum_ieum.domain.emergencyRequest.EmergencyRequest;
import com.develokit.maeum_ieum.domain.emergencyRequest.EmergencyType;
import com.develokit.maeum_ieum.domain.user.caregiver.Caregiver;
import com.develokit.maeum_ieum.domain.user.elderly.Elderly;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.eq;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
@ActiveProfiles("dev")
class WebSocketServiceTest {

    @InjectMocks
    private WebSocketService webSocketService;

    @Mock
    private SimpMessagingTemplate messagingTemplate;


    @Test
    void 요양사에게_긴급알림_전송되는지_테스트() throws Exception{
        //given
        Long caregiverId = 98L;
        Long elderlyId = 1L;
        String elderlyName = "토쿠노";
        String id = UUID.randomUUID().toString();
        String message = "토쿠노 어르신으로부터 긴급알림이 왔습니다";
        LocalDateTime today = LocalDateTime.now();
        String expectedDestination = "/topic/emergencyRequests/" + caregiverId;


        EmergencyRequest emergencyRequest = mock(EmergencyRequest.class);
        Caregiver caregiver = mock(Caregiver.class);
        Elderly elderly = mock(Elderly.class);

        when(emergencyRequest.getElderly()).thenReturn(elderly);
        when(elderly.getId()).thenReturn(elderlyId);
        when(caregiver.getId()).thenReturn(caregiverId);
        when(elderly.getName()).thenReturn(elderlyName);
        when(emergencyRequest.getCreatedDate()).thenReturn(today);
        when(emergencyRequest.getEmergencyType()).thenReturn(EmergencyType.CAREGIVER_NOTIFY);
        when(emergencyRequest.getMessage()).thenReturn(message);


        Map<String, Object> alertMessage = new HashMap<>();
        alertMessage.put("id", id);
        alertMessage.put("elderlyId", elderlyId);
        alertMessage.put("elderlyName", elderlyName);
        alertMessage.put("emergencyType", EmergencyType.CAREGIVER_NOTIFY.toString());
        alertMessage.put("message", message);
        alertMessage.put("timestamp", today);
        alertMessage.put("caregiverId", caregiverId);


        // when
        boolean result = webSocketService.sendEmergencyRequestToCaregiver(caregiver, emergencyRequest);

        // then
        assertTrue(result);
        System.out.println("messagingTemplate = " + messagingTemplate);
        verify(messagingTemplate).convertAndSend(eq(expectedDestination), eq(alertMessage));
    }

}