package com.develokit.maeum_ieum.config.loginUser;

import com.develokit.maeum_ieum.domain.user.caregiver.CareGiverRepository;
import com.develokit.maeum_ieum.domain.user.caregiver.Caregiver;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginUserService implements UserDetailsService {


    private final CareGiverRepository careGiverRepository;



    //실제 DB에 있는지 체크
    //DB에 없으면 new internalAuthenticationServiceException throw
    //DB에 있으면 Userdatils를 세션에 저장 -> 로그인 성공
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Caregiver caregiver = careGiverRepository.findByUsername(username)
                .orElseThrow(() -> new InternalAuthenticationServiceException("인증 실패"));
        return new LoginUser(caregiver); //세션에 담기
    }
}
