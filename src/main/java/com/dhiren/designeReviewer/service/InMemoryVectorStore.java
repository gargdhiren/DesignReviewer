package com.dhiren.designeReviewer.service;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class InMemoryVectorStore {
    private final Map<Long, List<Double>> chunkVectors=new HashMap<>();

    public void store(Long chunkId, List<Double> vector) {
        chunkVectors.put(chunkId, vector);
    }

    public Map<Long, List<Double>> getAll() {
        return chunkVectors;
    }
}
