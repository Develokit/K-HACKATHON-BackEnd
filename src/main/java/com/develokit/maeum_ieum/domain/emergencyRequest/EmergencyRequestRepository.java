package com.develokit.maeum_ieum.domain.emergencyRequest;

import com.develokit.maeum_ieum.domain.user.caregiver.Caregiver;
import com.develokit.maeum_ieum.dto.emergencyRequest.RespDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.develokit.maeum_ieum.dto.emergencyRequest.RespDto.EmergencyRequestListRespDto.*;

@Repository
public interface EmergencyRequestRepository extends JpaRepository<EmergencyRequest, Long> {


    @Query("select er from EmergencyRequest er " +
            "left join fetch er.caregiver c " +
            "left join fetch er.elderly e")
    Page<EmergencyRequest> findByCaregiver(Caregiver caregiver, Pageable pageable);
}
