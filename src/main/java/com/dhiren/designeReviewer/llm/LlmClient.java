package com.dhiren.designeReviewer.llm;

public interface LlmClient {
    String askQuestion(String context, String question);
}
