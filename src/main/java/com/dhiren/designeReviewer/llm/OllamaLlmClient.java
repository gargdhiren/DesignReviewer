package com.dhiren.designeReviewer.llm;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
public class OllamaLlmClient implements LlmClient {

    private final ChatClient chatClient;

    public OllamaLlmClient(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    @Override
    public String askQuestion(String context, String question) {

        String prompt = """
            You are a senior system design reviewer.
            
            Use ONLY the provided context to answer the question.
            If the answer is not present in the context, say:
            "Not found in document."
            
            For every statement, cite the chunk number in brackets.
            
            Context:
            %s
            
            Question:
            %s
            """.formatted(context, question);

        return chatClient.prompt()
                .user(prompt)
                .call()
                .content();
    }
}
