package com.develokit.maeum_ieum.domain.user;

import com.develokit.maeum_ieum.domain.base.BaseEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@NoArgsConstructor
@MappedSuperclass
@Getter
public abstract class User extends BaseEntity {

    private String name;//이름
    private String contact; //연락처
    @Enumerated(value = EnumType.STRING)
    private Gender gender; //성별

    private String imgUrl; //프로필 이미지
    private LocalDate birthDate; //생년 월일
    private String organization; //소속 기관

    public User(String name, String contact, Gender gender, String imgUrl, LocalDate birthDate, String organization) {
        this.name = name;
        this.contact = contact;
        this.gender = gender;
        this.imgUrl = imgUrl;
        this.birthDate = birthDate;
        this.organization = organization;
    }
}
