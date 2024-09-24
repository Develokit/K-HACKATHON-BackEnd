package com.develokit.maeum_ieum.domain.report;

import com.develokit.maeum_ieum.dto.report.RespDto;
import com.google.gson.Gson;
import org.junit.jupiter.api.Test;

import static com.develokit.maeum_ieum.dto.report.RespDto.*;
import static org.junit.jupiter.api.Assertions.*;

class ReportTest {

    @Test
    void 올바르게파싱되는경우() {
        String quantitativeAnalysis = "{\"healthStatusIndicator\":\"유우시쿤 건강상태 초 사이코🤍\",\"activityLevelIndicator\":\"유우시쿤 활동량 초 타카이🤍 일일 평균 걸음 수: 15,000보\",\"cognitiveFunctionIndicator\":\"인지 기능 테스트 점수: 25/30, 일상생활 수행 능력 양호\",\"lifeSatisfactionIndicator\":\"주관적 행복도 점수: 4/10, 유우시쿤 개선이 필요행ㅠㅠ!!\",\"psychologicalStabilityIndicator\":\"우울증 선별 검사 점수: 15/20, 전문가 상담 권장\",\"socialConnectivityIndicator\":\"주간 사회활동 참여 횟수: 4회, 사회적 관계 만족도 높음\",\"supportNeedsIndicator\":\"일상생활 지원 필요도: 중간, 유우시군 주 2회 방문 요양 서비스 권장🤍\"}";
        Gson gson = new Gson();

        QuantitativeAnalysis response
                = gson.fromJson(quantitativeAnalysis, QuantitativeAnalysis.class);

        System.out.println("response = " + response);
    }
}