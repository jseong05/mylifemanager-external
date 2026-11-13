package com.mlm.external;

import java.util.Locale;
import java.util.Random;

public class OfflineAiEngine {

    public static String getResponse(String userPrompt, String persona, int balance, int todayExpense) {
        String p = persona != null ? persona : "팩폭형 코치";
        String lower = userPrompt.toLowerCase(Locale.ROOT);

        // Keyword based smart responses
        if (lower.contains("지출") || lower.contains("돈") || lower.contains("예산") || lower.contains("잔액")) {
            if ("천사형 상담사".equals(p)) {
                return String.format(Locale.getDefault(), "현재 잔액은 %,d원이고, 오늘 지출은 %,d원이네요. 무리하지 말고 알뜰하게 잘 관리하고 계시니 조금만 더 힘내세요! ✨", balance, todayExpense);
            } else if ("냉철한 재무관".equals(p)) {
                return String.format(Locale.getDefault(), "재무 데이터 분석 결과 - 현재 잔액: %,d원, 금일 지출: %,d원입니다. 지출 예산 한도를 준수하십시오.", balance, todayExpense);
            } else {
                return String.format(Locale.getDefault(), "현재 남은 잔액이 %,d원인데 오늘 벌써 %,d원이나 썼다고? 정신 차려라 진짜 이러다 지갑 텅 빈다! 🚨", balance, todayExpense);
            }
        } else if (lower.contains("안녕") || lower.contains("반가워") || lower.contains("하이")) {
            if ("천사형 상담사".equals(p)) {
                return "안녕하세요! 오늘 하루도 고생 많으셨어요. 무엇을 도와드릴까요? 😊";
            } else if ("냉철한 재무관".equals(p)) {
                return "안녕하십니까. 자산 관리 시스템 AI 재무관입니다.";
            } else {
                return "오냐. 오늘도 지갑 단단히 붙들어 매고 있냐?";
            }
        } else if (lower.contains("절약") || lower.contains("모아") || lower.contains("저축")) {
            if ("천사형 상담사".equals(p)) {
                return "목표 저축을 향해 차근차근 나아가고 계시네요! 불필요한 카페인이나 간식비를 조금만 줄여보면 어떨까요?";
            } else if ("냉철한 재무관".equals(p)) {
                return "고정 지출을 제외한 가변 지출 항목을 20% 삭감하는 것을 권장합니다.";
            } else {
                return "절약은 입으로 하는 게 아니다! 당장 배달앱 지우고 냉장고 털어 먹어라 🍚";
            }
        } else {
            // General witty fallback based on persona
            String[] harsh = {
                    "그 질문 할 시간에 가계부나 한 번 더 들여다봐라!",
                    "정신 바짝 차려라! 돈은 썰물처럼 나가고 물 들어올 때는 노를 젓는 거야.",
                    "음... 그건 네 지갑이 직접 대답해 줄걸? 잔액을 확인해 봐라.",
                    "열심히 일해서 벌고 아껴서 모으는 것 외에 왕도는 없다!"
            };
            String[] angel = {
                    "고민이 많으시군요! 저와 함께 차근차근 해결해 나가요. 언제나 응원합니다! 🌸",
                    "오늘도 정말 수고 많으셨어요. 가끔은 자신을 위해 맛있는 걸 먹는 것도 좋지만, 저축도 잊지 마세요!",
                    "어떤 고민이든 편하게 이야기해 주세요. 다 잘 될 거예요! ✨"
            };
            String[] cold = {
                    "해당 질의에 대한 최적의 재무 솔루션을 산출 중입니다. 지출 내역을 입력하십시오.",
                    "자산 증식을 위해서는 철저한 예산 통제와 소비 억제가 필수 조건입니다.",
                    "데이터에 기반한 합리적인 소비 패턴을 유지해 주시기 바랍니다."
            };

            Random r = new Random();
            if ("천사형 상담사".equals(p)) {
                return angel[r.nextInt(angel.length)];
            } else if ("냉철한 재무관".equals(p)) {
                return cold[r.nextInt(cold.length)];
            } else {
                return harsh[r.nextInt(harsh.length)];
            }
        }
    }
}
