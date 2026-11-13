package com.mlm.external;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.mlm.external.databinding.ActivityAiChatBinding;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AiChatActivity extends AppCompatActivity {
    private ActivityAiChatBinding binding;
    private MyDataStore dataManager;
    private AiMessageAdapter adapter;
    private List<AiMessage> messageList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAiChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        dataManager = new MyDataStore(this);
        messageList = dataManager.getChatMessages();

        // If chat is empty, add welcome greeting from AI based on persona
        if (messageList.isEmpty()) {
            String persona = dataManager.getAiPersona();
            String greeting = "안녕하세요! 저는 당신의 AI 자산 관리사입니다. 무엇을 도와드릴까요?";
            if ("팩폭형 코치".equals(persona)) {
                greeting = "반갑다! 오늘 또 지갑 열기 전에 나한테 물어봐라. 팩폭으로 정신 번쩍 차리게 해줄 테니까.";
            } else if ("천사형 상담사".equals(persona)) {
                greeting = "안녕하세요! 오늘 하루도 고생 많으셨어요. 예산 계획이나 고민이 있다면 언제든 편하게 이야기해 주세요. ✨";
            } else if ("냉철한 재무관".equals(persona)) {
                greeting = "안녕하십니까. 수석 재무관입니다. 자산 현황 및 지출 통제에 관해 질의해 주십시오.";
            }
            messageList.add(new AiMessage(UUID.randomUUID().toString(), "ai", greeting, System.currentTimeMillis()));
            dataManager.saveChatMessages(messageList);
        }

        binding.rvChatMessages.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AiMessageAdapter(messageList);
        binding.rvChatMessages.setAdapter(adapter);
        binding.rvChatMessages.scrollToPosition(messageList.size() - 1);

        binding.btnSendAi.setOnClickListener(v -> {
            String text = binding.etAiInput.getText().toString().trim();
            if (text.isEmpty()) return;

            AiMessage userMsg = new AiMessage(UUID.randomUUID().toString(), "user", text, System.currentTimeMillis());
            messageList.add(userMsg);
            adapter.updateData(messageList);
            binding.rvChatMessages.scrollToPosition(messageList.size() - 1);
            binding.etAiInput.setText("");

            String persona = dataManager.getAiPersona();
            String contextPrompt = String.format("현재 잔액: %,d원, 오늘 지출: %,d원, 질문: %s",
                    dataManager.totalMoneyNow(), dataManager.getTodayExpenseAmount(), text);

            AiClient.generateAiResponse(contextPrompt, persona, dataManager.totalMoneyNow(), dataManager.getTodayExpenseAmount(), new AiClient.AiCallback() {
                @Override
                public void onSuccess(String responseText) {
                    AiMessage aiMsg = new AiMessage(UUID.randomUUID().toString(), "ai", responseText, System.currentTimeMillis());
                    messageList.add(aiMsg);
                    dataManager.saveChatMessages(messageList);
                    adapter.updateData(messageList);
                    binding.rvChatMessages.scrollToPosition(messageList.size() - 1);
                }

                @Override
                public void onError(String error) {
                    AiMessage aiMsg = new AiMessage(UUID.randomUUID().toString(), "ai", "오프라인 AI 응답: " + error, System.currentTimeMillis());
                    messageList.add(aiMsg);
                    dataManager.saveChatMessages(messageList);
                    adapter.updateData(messageList);
                    binding.rvChatMessages.scrollToPosition(messageList.size() - 1);
                }
            });
        });
    }
}
