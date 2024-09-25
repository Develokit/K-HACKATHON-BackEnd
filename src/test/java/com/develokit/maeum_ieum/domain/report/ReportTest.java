package com.develokit.maeum_ieum.domain.report;

import com.develokit.maeum_ieum.dto.report.RespDto;
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

    String monthlyResponse =
            "분석 완료: **월간 평가 지표 및 이유:**\n" +
                    "\n" +
                    "**HealthStatusIndicator: FAIR**\n" +
                    "이유: 첫 두 주 동안 건강에 특별한 문제가 없었으나, 마지막 주에는 피로감을 호소하며 건강 상태가 약간 악화되었습니다. 건강 상태에 변동이 있었기 때문에 FAIR로 평가합니다.\n" +
                    "\n" +
                    "**ActivityLevelIndicator: FAIR**\n" +
                    "이유: 첫 주와 마지막 주에는 활동 수준이 낮고 주로 실내에 머물렀지만, 두 번째 주에는 산책 등 신체 활동을 꾸준히 했습니다. 활동 수준이 변동되어 FAIR로 평가합니다.\n" +
                    "\n" +
                    "**CognitiveFunctionIndicator: GOOD**\n" +
                    "이유: 첫 두 주 동안 명확하고 논리적인 답변을 제공했고, 마지막 주에는 약간의 혼동을 보였으나 전반적으로 인지 기능이 양호했습니다. 인지 기능이 대체로 유지되어 GOOD으로 평가합니다.\n" +
                    "\n" +
                    "**LifeSatisfactionIndicator: FAIR**\n" +
                    "이유: 첫 주와 마지막 주에는 생활에 대한 특별한 만족감을 표현하지 않았고, 두 번째 주에만 만족감을 나타냈습니다. 전반적인 생활 만족도가 일관되지 않아 FAIR로 평가합니다.\n" +
                    "\n" +
                    "**PsychologicalStabilityIndicator: FAIR**\n" +
                    "이유: 첫 주에는 약간의 스트레스를 표현했고, 마지막 주에는 불안과 우울감을 나타냈습니다. 두 번째 주에 심리적으로 안정되었으나 전반적으로 불안정한 경향이 있어 FAIR로 평가합니다.\n" +
                    "\n" +
                    "**SocialConnectivityIndicator: FAIR**\n" +
                    "이유: 첫 두 주 동안 가족과 친구들과의 긍정적 상호작용이 있었으나, 마지막 주에는 거의 상호작용이 없었습니다. 사회적 연결성이 변동되었기 때문에 FAIR로 평가합니다.\n" +
                    "\n" +
                    "**SupportNeedsIndicator: GOOD**\n" +
                    "이유: 첫 두 주 동안 대부분 독립적으로 생활할 수 있었고, 마지막 주에 약간의 외부 지원이 필요했으나 전적으로 의존하지는 않았습니다. 대체로 독립성을 유지하여 GOOD으로 평가합니다.\n" +
                    "\n" +
                    "---\n" +
                    "\n" +
                    "**종합 평가:** \n" +
                    "노인의 건강 상태, 활동 수준, 인지 기능, 생활 만족도, 심리적 안정, 사회적 연결성이 한 달 동안 변동이 있었습니다. 특히 마지막 주에 건강과 심리 상태가 악화되는 경향이 보이므로, 주기적인 모니터링과 적절한 지원이 필요합니다. 첫 두 주는 안정적이었으나, 마지막 주에 불안정함이 두드러졌습니다. 한 달 동안의 데이터를 통해 더 나은 건강 관리와 사회적 연결성 유지 전략이 필요함을 시사합니다.\n";


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

    private static final Pattern SUMMARY_PATTERN = Pattern.compile("\\*\\*종합 평가\\:\\*\\*\\s*(.*?)\\s*(?=\\n|$)", Pattern.DOTALL);


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