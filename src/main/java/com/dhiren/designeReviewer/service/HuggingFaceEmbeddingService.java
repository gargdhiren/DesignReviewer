package com.dhiren.designeReviewer.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
@Profile("grok")
public class HuggingFaceEmbeddingService implements EmbeddingService {

    private static final String JINA_API_URL = "https://api.jina.ai/v1/embeddings";

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${JINA_API_KEY}")
    private String apiKey;

    @Override
    @SuppressWarnings("unchecked")
    public List<Double> embed(String text) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        Map<String, Object> body = Map.of(
                "model", "jina-embeddings-v3",
                "input", List.of(text),
                "task", "text-matching"
        );

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        // Response: { "data": [ { "embedding": [0.1, 0.2, ...] } ] }
        Map<String, Object> response = restTemplate.postForObject(
                JINA_API_URL, request, Map.class
        );

        if (response == null || !response.containsKey("data")) {
            throw new RuntimeException("Jina embedding returned empty response");
        }

        List<Map<String, Object>> data = (List<Map<String, Object>>) response.get("data");
        return (List<Double>) data.getFirst().get("embedding");
    }
}
