package com.develokit.maeum_ieum.service;

import com.develokit.maeum_ieum.domain.emergencyRequest.EmergencyRequest;
import com.develokit.maeum_ieum.domain.user.caregiver.Caregiver;
import com.develokit.maeum_ieum.ex.CustomApiException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WebSocketService {

    private final SimpMessagingTemplate simpMessagingTemplate;
    private final Logger log = LoggerFactory.getLogger(WebSocketService.class);

    public boolean sendEmergencyRequestToCaregiver(Caregiver caregiver, EmergencyRequest emergencyRequest) {
        String destination = "/topic/emergencyRequests/" + caregiver.getId();
        try {
            Map<String, Object> alertMessage = new HashMap<>();
            alertMessage.put("id", UUID.randomUUID().toString());
            alertMessage.put("elderlyId", emergencyRequest.getElderly().getId());
            alertMessage.put("elderlyName", emergencyRequest.getElderly().getName());
            alertMessage.put("emergencyType", emergencyRequest.getEmergencyType().toString());
            alertMessage.put("message", emergencyRequest.getMessage());
            alertMessage.put("timestamp", emergencyRequest.getCreatedDate().toString());
            alertMessage.put("caregiverId", caregiver.getId());

            simpMessagingTemplate.convertAndSend(destination, alertMessage);
            log.info("긴급 알림 요양사에게 전달: "+caregiver.getId());
            return true;
        }catch (Exception e){
            log.error("긴급 알림 요양사에게 전달 실패: "+caregiver.getId());
            throw new CustomApiException("긴급 알림 요양사에게 전달 실패: "+caregiver.getId(), HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
