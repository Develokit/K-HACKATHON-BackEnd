package com.develokit.maeum_ieum.domain.user.elderly;

import com.develokit.maeum_ieum.domain.assistant.Assistant;
import com.develokit.maeum_ieum.domain.user.Gender;
import com.develokit.maeum_ieum.domain.user.User;
import com.develokit.maeum_ieum.domain.user.caregiver.Caregiver;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Entity
@NoArgsConstructor
@Getter
public class Elderly extends User {

    @Id
    @Column(name = "elderly_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String healthInfo; // 특이 사항
    private String address; //주거지

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "caregiver_id")
    private Caregiver caregiver;

    @OneToOne(mappedBy = "elderly")
    private Assistant assistant;

    @Builder
    public Elderly(String name, String contact, Gender gender, String imgUrl, LocalDate birthDate, String organization, String healthInfo, String address, Caregiver caregiver, Assistant assistant) {
        super(name, contact, gender, imgUrl, birthDate, organization);
        this.healthInfo = healthInfo;
        this.address = address;
        this.caregiver = caregiver;
        this.assistant = assistant;
        caregiver.getElderlyList().add(this);
    }
}
