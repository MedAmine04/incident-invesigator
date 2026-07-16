package com.hps.pfa.incident_investigator.agent;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
import org.springframework.stereotype.Service;

@Service
public class SpecificationAgentService {

    private final ChatClient chatClient;

    public SpecificationAgentService(ChatClient.Builder chatClientBuilder, VectorStore vectorStore) {
        FilterExpressionBuilder b = new FilterExpressionBuilder();
        this.chatClient = chatClientBuilder
                .defaultAdvisors(QuestionAnswerAdvisor.builder(vectorStore)
                        .searchRequest(SearchRequest.builder()
                                .filterExpression(b.eq("corpus", "technical").build())
                                .topK(3)
                                .build())
                        .build())
                .build();
    }

    public String ask(String message) {
        return chatClient.prompt()
                .user(message)
                .call()
                .content();
    }
}