package com.develokit.maeum_ieum.service;

import com.develokit.maeum_ieum.domain.user.Gender;
import com.develokit.maeum_ieum.domain.user.Role;
import com.develokit.maeum_ieum.domain.user.caregiver.CareGiverRepository;
import com.develokit.maeum_ieum.domain.user.caregiver.Caregiver;
import com.develokit.maeum_ieum.dto.caregiver.ReqDto;
import com.develokit.maeum_ieum.dto.caregiver.RespDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static com.develokit.maeum_ieum.dto.caregiver.ReqDto.*;
import static com.develokit.maeum_ieum.dto.caregiver.RespDto.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class) //Mokito 테스트
class CaregiverServiceTest {

    @InjectMocks
    private CaregiverService caregiverService;
    @InjectMocks
    private ObjectMapper om;

    @Mock
    private CareGiverRepository careGiverRepository;

    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Mock
    private S3Service s3Service;


    @Test
    void join_test() throws Exception{
        //given
        JoinReqDto joinReqDto = new JoinReqDto();
        joinReqDto.setName("userA");
        joinReqDto.setContact("010-1234-5678");
        joinReqDto.setGender(Gender.FEMALE.toString());
        joinReqDto.setPassword("1234");
        joinReqDto.setUsername("user1111");
        joinReqDto.setImg(new MockMultipartFile("imgFile", new byte[0]));


        //when
        when(careGiverRepository.findByUsername(joinReqDto.getUsername())).thenReturn(Optional.empty());
        when(s3Service.uploadImage(joinReqDto.getImg())).thenReturn("imgUrl");
        when(bCryptPasswordEncoder.encode(joinReqDto.getPassword())).thenReturn("비밀번호 인코딩");


        Caregiver caregiver = Caregiver.builder()
                .username(joinReqDto.getUsername())
                .build();


        when(careGiverRepository.save(any())).thenReturn(caregiver);

        JoinRespDto joinRespDto = caregiverService.join(joinReqDto);
        String response = om.writeValueAsString(joinRespDto);

        //then
        assertNotNull(joinRespDto);
        assertThat(joinRespDto.getUsername()).isEqualTo(joinReqDto.getUsername());

    }

}