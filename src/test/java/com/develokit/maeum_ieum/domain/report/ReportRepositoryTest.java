package com.develokit.maeum_ieum.domain.report;

import com.develokit.maeum_ieum.domain.user.elderly.Elderly;
import com.develokit.maeum_ieum.domain.user.elderly.ElderlyRepository;
import com.develokit.maeum_ieum.dummy.DummyObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
/*
* JPA 관련 테스트 설정을 로드한다. 데이터소스 설정이 정상인지 JPA를 사용해서 생성, 수정, 삭제 등의 테스트를 할 수 있다. 실제 데이터베이스를 사용하지 않고 내장형을 사용해서 테스트할 수 있기 때문에 실제DB에 영향을 주지 않는다. 
기본적으로 임베디드 데이터베이스를 사용한다.
*/
//@DataJpaTest
@SpringBootTest
@ActiveProfiles("dev")
class ReportRepositoryTest extends DummyObject {

    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private ElderlyRepository elderlyRepository;

    @Autowired private EntityManager em;

    @Test
    @Rollback(value = false)
    @Transactional
    void 테스트용_주간보고서_생성하기() throws JsonProcessingException {
        Optional<Elderly> elderly = elderlyRepository.findById(34L);

        Elderly elderlyPS = elderly.get();

        Report report = mockWeeklyReport(elderlyPS);

        em.persist(report);
    }

    @Test
    @Rollback(value = false)
    @Transactional
    void 테스트용_월간보고서_생성하기() throws JsonProcessingException {
        Optional<Elderly> elderly = elderlyRepository.findById(34L);

        Elderly elderlyPS = elderly.get();

        Report report = mockMonthlyReport(elderlyPS);

        em.persist(report);
    }



}