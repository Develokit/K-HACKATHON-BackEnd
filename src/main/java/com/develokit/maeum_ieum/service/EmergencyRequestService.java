package com.develokit.maeum_ieum.service;
import com.develokit.maeum_ieum.domain.emergencyRequest.EmergencyRequest;
import com.develokit.maeum_ieum.domain.emergencyRequest.EmergencyRequestRepository;
import com.develokit.maeum_ieum.domain.emergencyRequest.EmergencyType;
import com.develokit.maeum_ieum.domain.user.caregiver.CareGiverRepository;
import com.develokit.maeum_ieum.domain.user.caregiver.Caregiver;
import com.develokit.maeum_ieum.domain.user.elderly.Elderly;
import com.develokit.maeum_ieum.domain.user.elderly.ElderlyRepository;
import com.develokit.maeum_ieum.dto.emergencyRequest.ReqDto;
import com.develokit.maeum_ieum.dto.emergencyRequest.RespDto;
import com.develokit.maeum_ieum.ex.CustomApiException;
import com.develokit.maeum_ieum.util.CustomUtil;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

import java.util.ArrayList;
import java.util.stream.Collectors;

import static com.develokit.maeum_ieum.dto.emergencyRequest.ReqDto.*;
import static com.develokit.maeum_ieum.dto.emergencyRequest.RespDto.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EmergencyRequestService {

    private final EmergencyRequestRepository emergencyRequestRepository;
    private final CareGiverRepository careGiverRepository;
    private final ElderlyRepository elderlyRepository;
    private final WebSocketService webSocketService;



    //TODO 긴급 알림 저장
    @Transactional
    public EmergencyRequestCreateRespDto createEmergencyRequest(Long elderlyId, Long caregiverId, EmergencyRequestCreateReqDto emergencyRequestCreateReqDto){
        //노인이 등록된 사용자인지 확인
        Elderly elderlyPS = elderlyRepository.findById(elderlyId).orElseThrow(
                () -> new CustomApiException("등록되지 않은 노인 사용자입니다.", HttpStatus.NOT_FOUND.value(), HttpStatus.NOT_FOUND)
        );

        Caregiver caregiverPS = careGiverRepository.findById(caregiverId).orElseThrow(
                () -> new CustomApiException("등록되지 않은 요양사 사용자입니다", HttpStatus.NOT_FOUND.value(), HttpStatus.NOT_FOUND)
        );

        if(elderlyPS.getCaregiver() != caregiverPS)
            throw new CustomApiException("담당 요양사가 아닌 요양사를 호출했습니다", HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN);

        //알림 내역 저장
        EmergencyRequest emergencyRequestPS = emergencyRequestRepository.save(EmergencyRequest.builder()
                .emergencyType(EmergencyType.fromString(emergencyRequestCreateReqDto.getEmergencyType()))
                .caregiver(caregiverPS)
                .elderly(elderlyPS)
                .build());

        //노인의 요양사에게 알림 발행
        boolean isSent = webSocketService.sendEmergencyRequestToCaregiver(caregiverPS, emergencyRequestPS);
        return new EmergencyRequestCreateRespDto(emergencyRequestPS, isSent);
    }


    //알림 화면 조회
    public EmergencyRequestListRespDto getEmergencyRequestList(String username, int page, int size){

        //요양사 조회
        Caregiver caregiverPS = careGiverRepository.findByUsername(username).orElseThrow(
                () -> new CustomApiException("등록되지 않은 요양사 사용자입니다", HttpStatus.NOT_FOUND.value(), HttpStatus.NOT_FOUND)
        );
        //페이징
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdDate"));

        Page<EmergencyRequest> emergencyRequestList = emergencyRequestRepository.findByCaregiver(caregiverPS, pageable);

        return new EmergencyRequestListRespDto(emergencyRequestList, size);
    }


}
