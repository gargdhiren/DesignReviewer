package com.dhiren.designeReviewer.service;

import java.util.List;
import java.util.Vector;

public class VectorUtils {
    public static double CosineSimilarity(List<Double> v1, List<Double> v2) {
            double dot = 0.0;
            double norm1 = 0.0;
            double norm2 = 0.0;

            for (int i = 0; i < v1.size(); i++) {
                dot += v1.get(i) * v2.get(i);
                norm1 += Math.pow(v1.get(i), 2);
                norm2 += Math.pow(v2.get(i), 2);
            }

            return dot / (Math.sqrt(norm1) * Math.sqrt(norm2));
    }
}
