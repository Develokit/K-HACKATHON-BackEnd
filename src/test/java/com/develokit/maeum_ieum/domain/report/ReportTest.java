package com.develokit.maeum_ieum.domain.report;

import com.develokit.maeum_ieum.dto.report.RespDto;
import com.develokit.maeum_ieum.service.report.IndicatorResult;
import com.develokit.maeum_ieum.service.report.MonthlyReportAnalysisService;
import com.develokit.maeum_ieum.service.report.WeeklyReportAnalysisService;
import com.google.gson.Gson;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.develokit.maeum_ieum.dto.report.RespDto.*;
import static com.develokit.maeum_ieum.service.report.WeeklyReportAnalysisService.*;
import static org.junit.jupiter.api.Assertions.*;

class ReportTest {

    String monthlyResponse = "### 월간 평가\n" +
            "\n" +
            "**ActivityLevelIndicator: FAIR**  \n" +
            "이유: 노인은 운동에 대한 귀찮음을 표현했지만, 운동 계획을 세우고 있어 일부 활동성을 유지하고 있습니다. 전반적으로 보다 더 적극적인 활동이 필요하지만, 회피하지 않고 운동을 고려하는 모습이 있습니다.\n" +
            "\n" +
            "**HealthStatusIndicator: GOOD**  \n" +
            "이유: 노인이 직접적으로 건강 문제를 언급하지 않았고, 운동 계획을 세우고 있다는 점에서 건강 상태가 양호하다고 볼 수 있습니다. 건강이 전반적으로 좋다고 판단되므로 GOOD으로 평가합니다.\n" +
            "\n" +
            "**CognitiveFunctionIndicator: GOOD**  \n" +
            "이유: 노인은 일관된 사고 흐름을 유지하고 운동에 대한 의사 결정을 내렸습니다. 이는 인지 기능이 양호하다는 것을 보여 줍니다. 따라서 GOOD로 평가합니다.\n" +
            "\n" +
            "**LifeSatisfactionIndicator: GOOD**  \n" +
            "이유: 운동 후의 계획이나 간식을 통해 긍정적인 생활 만족도를 나타냈습니다. 활동 후 만족감을 느끼는 모습이 삶의 질을 높이고 있다고 판단됩니다.\n" +
            "\n" +
            "**PsychologicalStabilityIndicator: GOOD**  \n" +
            "이유: 노인은 가벼운 유머를 사용하며 스트레스를 표현하지 않고 긍정적인 감정을 유지했습니다. 심리적으로 안정된 상태를 보여주어 좋은 평가를 받을 수 있습니다.\n" +
            "\n" +
            "**SocialConnectivityIndicator: GOOD**  \n" +
            "이유: AI와의 즐거운 대화를 통해 긍정적인 사회적 상호작용을 지속하고 있습니다. 이는 사회적 연결성이 잘 유지되고 있음을 나타냅니다.\n" +
            "\n" +
            "**SupportNeedsIndicator: EXCELLENT**  \n" +
            "이유: 노인은 운동이나 활동 계획을 독립적으로 고려하고 실행할 수 있는 능력을 보여주었고, 특별한 외부 지원이 필요하지 않은 상태입니다. 따라서 EXCELLENT으로 평가합니다.\n" +
            "\n" +
            "### 종합 평가:\n" +
            "전반적으로 노인은 건강, 인지 기능, 심리 안정성, 사회적 상호작용 등에서 긍정적인 상태를 보였습니다. 운동 계획을 세우고 이를 실행에 옮기고자 하는 의지 또한 좋은 징후입니다. 앞으로 더욱 활발한 활동과 사회적 상호작용을 통해 긍정적인 상태를 지속하고 개선할 수 있도록 장려할 필요가 있습니다.\n";

    private static final Pattern W_SUMMARY_PATTERN = Pattern.compile("### 종합 평가\\s*(.*?)\\s*$", Pattern.DOTALL);

    String response = "분석 완료: ### HealthStatusIndicator: GOOD\n" +
            "**이유:** 노인이 건강 문제를 언급하지 않았으며, 그림 그리기와 같은 활동을 할 계획을 세우는 모습에서 전반적인 건강 상태가 양호함을 보였습니다.\n" +
            "\n" +
            "### ActivityLevelIndicator: GOOD\n" +
            "**이유:** 노인은 그림 그리기를 시도하려고 하며, 적극적으로 참여할 창의적 활동에 대해 논의하고 계획합니다.\n" +
            "\n" +
            "### CognitiveFunctionIndicator: GOOD\n" +
            "**이유:** 노인은 일관된 사고 흐름을 유지하며, 새로운 주제에 대해 논리적으로 고민하고 답변할 수 있었습니다.\n" +
            "\n" +
            "### LifeSatisfactionIndicator: GOOD\n" +
            "**이유:** 노인은 새로운 활동을 시도하며 긍정적 반응을 보였고, 전반적으로 생활에 만족감을 나타내었습니다.\n" +
            "\n" +
            "### PsychologicalStabilityIndicator: GOOD\n" +
            "**이유:** 노인은 대화 중 불안이나 스트레스를 나타내지 않았고, 안정적이고 편안한 상태를 보였습니다.\n" +
            "\n" +
            "### SocialConnectivityIndicator: GOOD\n" +
            "**이유:** 노인은 AI와의 상호작용을 통해 정서적 만족을 얻으며, 긍정적인 사회적 관계를 유지하고 있습니다.\n" +
            "\n" +
            "### SupportNeedsIndicator: EXCELLENT\n" +
            "**이유:** 노인은 독립적으로 활동 계획을 세우고 실행할 능력을 보였으며, 외부 지원이 필요하지 않습니다.\n" +
            "\n" +
            "### 종합 평가\n" +
            "노인은 건강 상태가 양호하고, 창의적 활동에 관심을 가지며 이를 실행할 능력을 갖추고 있습니다. 대화 중 일관된 사고 흐름과 긍정적인 감정 상태를 유지하며, 사회적 연결성도 잘 유지하고 있습니다. 현재 상황으로 볼 때, 노인은 독립적으로 생활할 수 있으며 특별한 외부 지원이 필요하지 않는 상태입니다. 노인의 활동 의욕과 인지 기능이 양호하여 전반적인 생활 만족도가 높음을 알 수 있습니다.\n";

    private static final Pattern PATTERN = Pattern.compile("\\*\\*(.*?)\\: \\s*(.*?)\\s*\\*\\*\\s*이유: (.*?)\\n(?=\\n|$)");

    //private static final Pattern SUMMARY_PATTERN = Pattern.compile("\\*\\*종합 평가\\:\\*\\*\\s*(.*?)\\s*(?=\\n|$)", Pattern.DOTALL);
    private static final Pattern SUMMARY_PATTERN = Pattern.compile("### 종합 평가:\\s*(.*)", Pattern.DOTALL);


    @Test
    void 월간보고서파싱테스트(){
        Map<String, IndicatorResult> resultMap = new HashMap<>();

        Matcher matcher = PATTERN.matcher(monthlyResponse);

        // 각 지표와 이유 추출
        while (matcher.find()) {
            String indicator = matcher.group(1).trim();
            String value = matcher.group(2).trim();
            String reason = matcher.group(3).trim();
            // 지표명과 값이 제대로 분리된 상태로 저장
            System.out.println("indicator = " + indicator);
            System.out.println("value = " + value);
            System.out.println("reason = " + reason);
            System.out.println();
            resultMap.put(indicator, new IndicatorResult(value, reason));
        }
        // 종합 평가 추출
        Matcher summaryMatcher = SUMMARY_PATTERN.matcher(monthlyResponse);
        if (summaryMatcher.find()) {
            String summary = summaryMatcher.group(1).trim();
            System.out.println("summary = " + summary);
        }
    }

    @Test
    void 파싱테스트(){
        Map<String, IndicatorResult> resultMap = new HashMap<>();

        // 각 섹션을 "**이유:**"를 기준으로 분리
        String[] sections = response.split("\\n\\n");

        for (String section : sections) {
            if (section.contains("**")) {
                // 지표와 값을 추출하는 부분 (지표명과 값을 ":" 기준으로 분리)
                String[] indicatorAndReason = section.split("\\*\\*이유:\\*\\*");
                if (indicatorAndReason.length == 2) {
                    String indicatorPart = indicatorAndReason[0].trim(); // 지표명과 값
                    String reason = indicatorAndReason[1].trim(); // 이유

                    // "###" 제거 및 지표명과 값을 분리 (예: "### HealthStatusIndicator: GOOD")
                    String[] indicatorInfo = indicatorPart.split(":");
                    if (indicatorInfo.length == 2) {
                        String indicator = indicatorInfo[0].replace("###", "").trim(); // 지표명
                        String value = indicatorInfo[1].trim(); // 값

                        // 지표명과 값이 제대로 분리된 상태로 저장
                        System.out.println("indicator = " + indicator);
                        System.out.println("value = " + value);
                        System.out.println("reason = " + reason);
                        System.out.println();
                        resultMap.put(indicator, new IndicatorResult(value, reason));
                    }
                }
            }
        }

//        for (String s : resultMap.keySet()) {
//            System.out.println(s +": "+resultMap.get(s).getReason());
//        }
    }
    @Test
    void extractSummary() {
        Matcher summaryMatcher = W_SUMMARY_PATTERN.matcher(response);
        if (summaryMatcher.find()) {
            String summary = summaryMatcher.group(1).trim();// 종합 평가 내용 반환
            System.out.println("summary = " + summary);
        }
    }


    @Test
    void 올바르게파싱되는경우() {
        String quantitativeAnalysis = "{\"healthStatusIndicator\":\"유우시쿤 건강상태 초 사이코🤍\",\"activityLevelIndicator\":\"유우시쿤 활동량 초 타카이🤍 일일 평균 걸음 수: 15,000보\",\"cognitiveFunctionIndicator\":\"인지 기능 테스트 점수: 25/30, 일상생활 수행 능력 양호\",\"lifeSatisfactionIndicator\":\"주관적 행복도 점수: 4/10, 유우시쿤 개선이 필요행ㅠㅠ!!\",\"psychologicalStabilityIndicator\":\"우울증 선별 검사 점수: 15/20, 전문가 상담 권장\",\"socialConnectivityIndicator\":\"주간 사회활동 참여 횟수: 4회, 사회적 관계 만족도 높음\",\"supportNeedsIndicator\":\"일상생활 지원 필요도: 중간, 유우시군 주 2회 방문 요양 서비스 권장🤍\"}";
        Gson gson = new Gson();

        QuantitativeAnalysis response
                = gson.fromJson(quantitativeAnalysis, QuantitativeAnalysis.class);

        System.out.println("response = " + response);
    }
}