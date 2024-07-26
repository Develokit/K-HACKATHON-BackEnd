package com.develokit.maeum_ieum.service;

import com.develokit.maeum_ieum.domain.user.Gender;
import com.develokit.maeum_ieum.domain.user.caregiver.CareGiverRepository;
import com.develokit.maeum_ieum.domain.user.caregiver.Caregiver;
import com.develokit.maeum_ieum.dto.caregiver.RespDto;
import com.develokit.maeum_ieum.dto.caregiver.RespDto.JoinRespDto;
import com.develokit.maeum_ieum.ex.CustomApiException;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.Optional;

import static com.develokit.maeum_ieum.dto.caregiver.ReqDto.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CaregiverService {

    private final CareGiverRepository careGiverRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final S3Service s3Service;

    @Transactional
    public JoinRespDto join(JoinReqDto joinReqDto){

        //아이디 중복 검사
        Optional<Caregiver> caregiverOP = careGiverRepository.findByUsername(joinReqDto.getUsername());
        if(caregiverOP.isPresent()) throw new CustomApiException("이미 존재하는 아이디 입니다");
        //이미지 저장
        String imgUrl = s3Service.uploadImage(joinReqDto.getImg());

        //비밀번호 인코딩
        Caregiver caregiverPS = careGiverRepository.save(joinReqDto.toEntity(bCryptPasswordEncoder, imgUrl));

        //DTO 반환
        return new JoinRespDto(caregiverPS);
    }



}
