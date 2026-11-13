package com.mlm.external;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AiClient {
    private static final String API_KEY = BuildConfig.AI_API_KEY;
    private static final String ENDPOINT = "https://api.groq.com/openai/v1/chat/completions";
    private static final String MODEL_NAME = "llama-3.1-8b-instant";
    private static final ExecutorService EXECUTOR = Executors.newSingleThreadExecutor();

    public interface AiCallback {
        void onSuccess(String responseText);
        void onError(String error);
    }

    public static void generateAiResponse(String userPrompt, String persona, int balance, int todayExpense, AiCallback callback) {
        Handler handler = new Handler(Looper.getMainLooper());

        Log.d("AiClient", "API_KEY check: " + (API_KEY != null && API_KEY.length() > 6 ? API_KEY.substring(0, 6) + "..." : API_KEY));

        if (API_KEY == null || API_KEY.isEmpty() || !API_KEY.startsWith("gsk_")) {
            Log.w("AiClient", "API_KEY is missing or invalid! Using offline engine.");
            String offlineReply = OfflineAiEngine.getResponse(userPrompt, persona, balance, todayExpense);
            handler.post(() -> callback.onSuccess(offlineReply));
            return;
        }

        EXECUTOR.execute(() -> {
            HttpURLConnection conn = null;
            try {
                URL url = new URL(ENDPOINT);
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");
                conn.setRequestProperty("Authorization", "Bearer " + API_KEY);
                conn.setConnectTimeout(8000);
                conn.setReadTimeout(8000);
                conn.setDoOutput(true);

                String personaInstruction = getPersonaInstruction(persona);
                String systemContext = String.format(Locale.getDefault(), "지침: %s\n현재 사용자 재무 상태 - 잔액: %,d원, 오늘 지출: %,d원",
                        personaInstruction, balance, todayExpense);

                JsonArray messagesArray = new JsonArray();

                JsonObject systemMsg = new JsonObject();
                systemMsg.addProperty("role", "system");
                systemMsg.addProperty("content", systemContext);
                messagesArray.add(systemMsg);

                JsonObject userMsg = new JsonObject();
                userMsg.addProperty("role", "user");
                userMsg.addProperty("content", userPrompt);
                messagesArray.add(userMsg);

                JsonObject requestBody = new JsonObject();
                requestBody.addProperty("model", MODEL_NAME);
                requestBody.add("messages", messagesArray);
                requestBody.addProperty("temperature", 0.7);

                String jsonPayload = requestBody.toString();

                OutputStream os = conn.getOutputStream();
                os.write(jsonPayload.getBytes("UTF-8"));
                os.flush();
                os.close();

                int responseCode = conn.getResponseCode();
                Log.d("AiClient", "Groq Response Code: " + responseCode);

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
                    JsonArray choices = jsonResponse.getAsJsonArray("choices");
                    if (choices != null && !choices.isEmpty()) {
                        JsonObject choice = choices.get(0).getAsJsonObject();
                        JsonObject message = choice.getAsJsonObject("message");
                        if (message != null && message.has("content")) {
                            String aiText = message.get("content").getAsString();
                            handler.post(() -> callback.onSuccess(aiText.trim()));
                            return;
                        }
                    }
                    String offlineReply = OfflineAiEngine.getResponse(userPrompt, persona, balance, todayExpense);
                    handler.post(() -> callback.onSuccess(offlineReply));
                } else {
                    BufferedReader errIn = new BufferedReader(new InputStreamReader(conn.getErrorStream(), "UTF-8"));
                    StringBuilder errResp = new StringBuilder();
                    String line;
                    while ((line = errIn.readLine()) != null) {
                        errResp.append(line);
                    }
                    errIn.close();

                    Log.e("AiClient", "Groq API Error: " + errResp.toString());
                    String errorMsg = "Groq API 에러 (" + responseCode + "): " + errResp.toString();
                    handler.post(() -> callback.onSuccess(errorMsg));
                }
            } catch (Exception e) {
                Log.e("AiClient", "Groq Network Exception", e);
                String errorMsg = "Groq 네트워크 에러: " + e.getMessage();
                handler.post(() -> callback.onSuccess(errorMsg));
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
