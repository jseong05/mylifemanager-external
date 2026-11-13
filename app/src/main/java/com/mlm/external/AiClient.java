package com.mlm.external;

import android.os.Handler;
import android.os.Looper;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AiClient {
    private static final String API_KEY = BuildConfig.AI_API_KEY;
    private static final String ENDPOINT = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=";
    private static final ExecutorService EXECUTOR = Executors.newSingleThreadExecutor();

    public interface AiCallback {
        void onSuccess(String responseText);
        void onError(String error);
    }

    public static void generateAiResponse(String userPrompt, String persona, AiCallback callback) {
        Handler handler = new Handler(Looper.getMainLooper());

        if (API_KEY == null || API_KEY.isEmpty()) {
            handler.post(() -> callback.onError("AI API 키(aiApiKey)가 설정되지 않았습니다. local.properties를 확인해주세요."));
            return;
        }

        EXECUTOR.execute(() -> {
            HttpURLConnection conn = null;
            try {
                URL url = new URL(ENDPOINT + API_KEY);
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");
                conn.setConnectTimeout(8000);
                conn.setReadTimeout(8000);
                conn.setDoOutput(true);

                // Build Gemini Request Payload
                String personaInstruction = getPersonaInstruction(persona);
                String fullPrompt = personaInstruction + "\n사용자 질문: " + userPrompt;

                JsonObject contentObj = new JsonObject();
                JsonArray partsArray = new JsonArray();
                JsonObject partObj = new JsonObject();
                partObj.addProperty("text", fullPrompt);
                partsArray.add(partObj);
                contentObj.add("parts", partsArray);

                JsonArray contentsArray = new JsonArray();
                contentsArray.add(contentObj);

                JsonObject requestBody = new JsonObject();
                requestBody.add("contents", contentsArray);

                String jsonPayload = requestBody.toString();

                OutputStream os = conn.getOutputStream();
                os.write(jsonPayload.getBytes("UTF-8"));
                os.flush();
                os.close();

                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                    StringBuilder response = new StringBuilder();
                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();

                    Gson gson = new Gson();
                    JsonObject jsonResponse = gson.fromJson(response.toString(), JsonObject.class);
                    JsonArray candidates = jsonResponse.getAsJsonArray("candidates");
                    if (candidates != null && candidates.size() > 0) {
                        JsonObject candidate = candidates.get(0).getAsJsonObject();
                        JsonObject content = candidate.getAsJsonObject("content");
                        JsonArray parts = content.getAsJsonArray("parts");
                        if (parts != null && parts.size() > 0) {
                            String aiText = parts.get(0).getAsJsonObject().get("text").getAsString();
                            handler.post(() -> callback.onSuccess(aiText.trim()));
                            return;
                        }
                    }
                    handler.post(() -> callback.onError("AI 응답 파싱 오류"));
                } else {
                    handler.post(() -> callback.onError("AI API 오류 (" + responseCode + ")"));
                }
            } catch (Exception e) {
                handler.post(() -> callback.onError("네트워크 오류: " + e.getMessage()));
            } finally {
                if (conn != null) {
                    try {
                        conn.disconnect();
                    } catch (Exception ignored) {}
                }
            }
        });
    }

    private static String getPersonaInstruction(String persona) {
        if (persona == null) persona = "팩폭형 코치";
        switch (persona) {
            case "천사형 상담사":
                return "너는 친절하고 따뜻한 재무 상담사이오. 사용자를 다정하게 격려하고 칭찬하며 부드럽게 조언해 주시오.";
            case "냉철한 재무관":
                return "너는 냉철하고 이성적인 수석 재무관이오. 객관적인 데이터와 통계를 바탕으로 엄격하고 전문적인 분석을 제공해 주시오.";
            case "팩폭형 코치":
            default:
                return "너는 뼈를 때리는 유쾌한 팩폭형 자산 관리 코치이오. 사용자의 지출 습관을 유쾌하게 지적하며 정신 바짝 차리게 팩폭을 날려주오.";
        }
    }
}
