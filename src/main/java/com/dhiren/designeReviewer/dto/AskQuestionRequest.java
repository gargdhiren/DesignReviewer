package com.dhiren.designeReviewer.dto;

public class AskQuestionRequest {

    private String question;

    public AskQuestionRequest() {
        // Required for Jackson (JSON → Java conversion)
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }
}
