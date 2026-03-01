package com.dhiren.designeReviewer.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class EmbeddingConverter {
        private final ObjectMapper objectMapper = new ObjectMapper();

        public String toJson(List<Double> vector) {
            try {
                return objectMapper.writeValueAsString(vector);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        public List<Double> fromJson(String json) {
            try {
                return objectMapper.readValue(
                        json,
                        objectMapper.getTypeFactory()
                                .constructCollectionType(List.class, Double.class)
                );
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
}
