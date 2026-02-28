package com.dhiren.designeReviewer.dto;

import java.util.List;

public class AnswerResponse {
    private String answer;
    private List<Long> citations;

    public AnswerResponse(String answer, List<Long> citations) {
        this.answer = answer;
        this.citations=citations;
    }

    public String getAnswer() { return answer; }
    public List<Long> getCitations() { return citations; }
}
