package com.develokit.maeum_ieum.dummy;

import com.develokit.maeum_ieum.domain.assistant.Assistant;
import com.develokit.maeum_ieum.domain.emergencyRequest.EmergencyRequest;
import com.develokit.maeum_ieum.domain.emergencyRequest.EmergencyType;
import com.develokit.maeum_ieum.domain.message.Message;
import com.develokit.maeum_ieum.domain.message.MessageType;
import com.develokit.maeum_ieum.domain.report.Report;
import com.develokit.maeum_ieum.domain.report.ReportType;
import com.develokit.maeum_ieum.domain.user.Gender;
import com.develokit.maeum_ieum.domain.user.Role;
import com.develokit.maeum_ieum.domain.user.caregiver.Caregiver;
import com.develokit.maeum_ieum.domain.user.elderly.Elderly;
import com.develokit.maeum_ieum.domain.user.elderly.EmergencyContactInfo;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDate;

public class DummyObject {

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
                .id(1L)
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

    protected Report newMockWeeklyReport(Elderly elderly){
        return Report.builder()
                .elderly(elderly)
                .id(1L)
                .reportType(ReportType.WEEKLY)
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
                .id(elderlyId)
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
