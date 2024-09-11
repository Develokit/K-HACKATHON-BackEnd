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
        String expectedMessage = "토쿠노으로부터 긴급알림이 왔습니다";
        String expectedDestination = "/topic/emergencyRequests/" + caregiverId;

        Caregiver caregiver = mock(Caregiver.class);
        when(caregiver.getId()).thenReturn(caregiverId);

        EmergencyRequest emergencyRequest = mock(EmergencyRequest.class);
        when(emergencyRequest.getMessage()).thenReturn(expectedMessage);

        // when
        boolean result = webSocketService.sendEmergencyRequestToCaregiver(caregiver, emergencyRequest);

        // then
        assertTrue(result);
        verify(messagingTemplate).convertAndSend(eq(expectedDestination), eq(expectedMessage));
    }

}