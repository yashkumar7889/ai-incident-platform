package com.aiplatform.sentinel.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.ai.chat.client.ChatClient;

import com.aiplatform.sentinel.service.AiService;

@Service
public class AiServiceImpl implements AiService {

    private final ChatClient chatClient;

    public AiServiceImpl(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    @Override
    public String chat(String prompt) {

        return chatClient.prompt()
                .user(prompt)
                .call()
                .content();
    }

    @Override
    public String summarizeIncident(String description) {

        String prompt = """
                You are an experienced Site Reliability Engineer.

                Summarize the following incident in one concise sentence.

                Incident:
                %s
                """.formatted(description);

        return chatClient.prompt()
                .user(prompt)
                .call()
                .content();
    }
}