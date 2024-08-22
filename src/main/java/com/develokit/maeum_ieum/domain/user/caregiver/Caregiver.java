package com.develokit.maeum_ieum.domain.user.caregiver;

import com.develokit.maeum_ieum.domain.assistant.Assistant;
import com.develokit.maeum_ieum.domain.user.Gender;
import com.develokit.maeum_ieum.domain.user.Role;
import com.develokit.maeum_ieum.domain.user.elderly.Elderly;
import com.develokit.maeum_ieum.domain.user.User;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@AllArgsConstructor
public class Caregiver extends User {

    @Id
    @Column(name = "caregiver_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    @Column(nullable = false, length = 60)
    private String password;

    @OneToMany(mappedBy = "caregiver")
    private List<Assistant> assistantList = new ArrayList<>();

    @OneToMany(mappedBy = "caregiver")
    private List<Elderly> elderlyList = new ArrayList<>();

    @Builder
    public Caregiver(String name, String contact, Gender gender, String imgUrl, LocalDate birthDate, String organization, String username, String password, Role role) {
        super(name, contact, gender, imgUrl, birthDate, organization, role);
        this.username = username;
        this.password = password;
    }

}
