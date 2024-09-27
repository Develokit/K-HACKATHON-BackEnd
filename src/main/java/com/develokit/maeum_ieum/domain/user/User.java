package com.develokit.maeum_ieum.domain.user;

import com.develokit.maeum_ieum.domain.base.BaseEntity;
import com.develokit.maeum_ieum.service.CaregiverService;
import com.develokit.maeum_ieum.util.CustomUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@MappedSuperclass
@Getter
public abstract class User extends BaseEntity {

    private String name;//이름
    private String contact; //연락처
    @Enumerated(value = EnumType.STRING)
    private Gender gender; //성별

    private String imgUrl; //프로필 이미지

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy년 MM월 dd일")
    private LocalDate birthDate; //생년 월일

    private String organization; //소속 기관
    @Enumerated(value = EnumType.STRING)
    private Role role;

    public User(String name, String contact, Gender gender, String imgUrl, LocalDate birthDate, String organization, Role role) {
        this.name = name;
        this.contact = contact;
        this.gender = gender;
        this.imgUrl = imgUrl;
        this.birthDate = birthDate;
        this.organization = organization;
        this.role = role;
    }

    protected void updateCaregiverInfo(String name, String gender, String organization, String birthDate, String contact){
        if(name != null) this.name = name;
        if(gender != null) this.gender = Gender.valueOf(gender);
        if(organization!=null) this.organization = organization;
        if(birthDate != null) this.birthDate = CustomUtil.StringToLocalDate(String.valueOf(birthDate));
        if(contact != null) this.contact = contact;
    }
    protected void updateElderlyInfo(String name, String gender, String birthDate, String contact){
        if(name != null) this.name = name;
        if(gender != null) this.gender = Gender.valueOf(gender);
        if(birthDate != null) this.birthDate = CustomUtil.StringToLocalDate(String.valueOf(birthDate));
        if(contact != null) this.contact = contact;
    }

    protected void updateOrganizationByCaregiver(String organization){
        this.organization = organization;
    }

    protected void updateImg(String imgUrl){
        this.imgUrl = imgUrl;
    }
}
