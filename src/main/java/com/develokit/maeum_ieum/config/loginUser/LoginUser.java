package com.develokit.maeum_ieum.config.loginUser;

import com.develokit.maeum_ieum.domain.user.caregiver.Caregiver;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;

@Getter
@RequiredArgsConstructor
public class LoginUser implements UserDetails {

    private final Caregiver caregiver;

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority>authorities = new ArrayList<>();
        authorities.add(() -> "ROLE_"+caregiver.getRole());

        return authorities;
    }

    @Override
    public String getPassword() {
        return caregiver.getPassword();
    }

    @Override
    public String getUsername() {
        return caregiver.getUsername();
    }
}
