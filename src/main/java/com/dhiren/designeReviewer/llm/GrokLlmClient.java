package com.dhiren.designeReviewer.llm;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("grok")
public class GrokLlmClient implements LlmClient {

    private final ChatClient chatClient;

    public GrokLlmClient(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    @Override
    public String askQuestion(String context, String question) {

        String prompt = """
            You are a senior system design reviewer.

            Use ONLY the provided context to answer the question.
            Give a clear, concise answer in plain language.
            If the answer is not present in the context, say: "Not found in document."

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
