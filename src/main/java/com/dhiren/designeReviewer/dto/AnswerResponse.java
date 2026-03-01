package com.dhiren.designeReviewer.dto;

import java.util.List;

public class AnswerResponse {
    private String answer;
    private List<Long> citations;
    private Long sessionId;

    public AnswerResponse(String answer, List<Long> citations, Long sessionId) {
        this.answer = answer;
        this.citations=citations;
        this.sessionId=sessionId;
    }

    public String getAnswer() { return answer; }
    public List<Long> getCitations() { return citations; }
    public Long getSessionId() { return sessionId; }
}
