package com.develokit.maeum_ieum.domain.emergencyRequest;

import com.develokit.maeum_ieum.domain.user.caregiver.Caregiver;
import com.develokit.maeum_ieum.domain.user.elderly.Elderly;
import com.develokit.maeum_ieum.dto.emergencyRequest.RespDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.develokit.maeum_ieum.dto.emergencyRequest.RespDto.EmergencyRequestListRespDto.*;

@Repository
public interface EmergencyRequestRepository extends JpaRepository<EmergencyRequest, Long> {


    @Query("select er from EmergencyRequest er " +
            "left join fetch er.elderly e " +
            "where er.caregiver = :caregiver")
    Page<EmergencyRequest> findByCaregiver(@Param("caregiver")Caregiver caregiver, Pageable pageable);

    List<EmergencyRequest> findByElderly(Elderly elderly);

//    @Modifying(clearAutomatically = true)
//    @Query("delete from EmergencyRequest er where er.elderly = :elderly")
//    void deleteAllByElderly(@Param("elderly") Elderly elderly);

    @Modifying(clearAutomatically = true)
    @Query("delete from EmergencyRequest er where er.elderly = :elderly")
    int deleteAllByElderly(@Param("elderly") Elderly elderly);
    @Query("SELECT er FROM EmergencyRequest er JOIN FETCH er.elderly e WHERE e = :elderly")
    List<EmergencyRequest> findByElderlyWithFetch(@Param("elderly") Elderly elderly);


}
