package com.dhiren.designeReviewer.dto;

public class AskQuestionRequest {

    private String question;
    private Long sessionId;

    public AskQuestionRequest() {
        // Required for Jackson (JSON → Java conversion)
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }
}
