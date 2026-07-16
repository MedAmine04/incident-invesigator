package com.hps.pfa.incident_investigator.agent;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class TechnicalAgentService {

    private final ChatClient chatClient;

    public TechnicalAgentService(ChatClient.Builder chatClientBuilder, TransactionTools transactionTools) {
        this.chatClient = chatClientBuilder
                .defaultTools(transactionTools)
                .build();
    }

    public String ask(String message) {
        return chatClient.prompt()
                .user(message)
                .call()
                .content();
    }
}