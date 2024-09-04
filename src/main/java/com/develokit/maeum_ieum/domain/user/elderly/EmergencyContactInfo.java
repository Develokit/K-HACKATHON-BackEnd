package com.develokit.maeum_ieum.domain.user.elderly;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Embeddable
@NoArgsConstructor
@Getter
@AllArgsConstructor
@Builder
public class EmergencyContactInfo {

    protected String emergencyName;
    protected String emergencyContact;
    protected String relationship;


}
