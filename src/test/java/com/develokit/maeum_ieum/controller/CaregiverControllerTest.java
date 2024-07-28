package com.develokit.maeum_ieum.controller;

import com.develokit.maeum_ieum.domain.user.caregiver.CareGiverRepository;
import com.develokit.maeum_ieum.dummy.DummyObject;
import com.develokit.maeum_ieum.service.CaregiverService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDate;
import static com.develokit.maeum_ieum.dto.caregiver.ReqDto.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;


@AutoConfigureMockMvc
@ActiveProfiles("dev")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
class CaregiverControllerTest extends DummyObject {
    @Autowired private CaregiverService caregiverService;
    @Autowired private CareGiverRepository careGiverRepository;
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper om;

    @Test
    public void join_test() throws Exception{
        //given
        JoinReqDto joinReqDto = new JoinReqDto();
        joinReqDto.setName("userA");
        joinReqDto.setUsername("userA1111");
        joinReqDto.setPassword("userA1111");
        joinReqDto.setBirthDate(LocalDate.now().toString());
        joinReqDto.setImg(null);

        String requestBody = om.writeValueAsString(joinReqDto);

        //when
        ResultActions resultAction = mvc.perform(
                post("/caregivers")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON));

        String responseBody = resultAction.andReturn().getResponse().getContentAsString();
        System.out.println("responseBody = " + responseBody);
    }

}