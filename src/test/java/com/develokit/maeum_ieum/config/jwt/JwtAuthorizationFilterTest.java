package com.develokit.maeum_ieum.config.jwt;

import com.develokit.maeum_ieum.config.loginUser.LoginUser;
import com.develokit.maeum_ieum.domain.user.Role;
import com.develokit.maeum_ieum.domain.user.caregiver.Caregiver;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc //mock 환경에 mvc가 new돼서 들어감
@ActiveProfiles("dev")
public class JwtAuthorizationFilterTest {

    @Autowired
    private MockMvc mvc;

    @Test
    void authorization_success_test() throws Exception{
        //given
        Caregiver caregiver = Caregiver.builder().username("yaewon0411").role(Role.ADMIN).build();
        LoginUser loginUser = new LoginUser(caregiver);
        String token = JwtProvider.create(loginUser);
        System.out.println("token = " + token);

        //when
        ResultActions resultActions = mvc.perform(get("/caregivers/elderlys").header(JwtVo.HEADER, token));

        String header = resultActions.andReturn().getResponse().getHeader("Authorization");
        System.out.println("header = " + header);


        //then

    }


}