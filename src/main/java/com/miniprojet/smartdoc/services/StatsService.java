package com.miniprojet.smartdoc.services;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.List;
import java.util.Map;

@Service
public class StatsService {

    private final RestTemplate restTemplate;
    private final String mcpUrl = "http://localhost:8899"; // MCP Server

    public StatsService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public Map<String, Double> calculateStats(List<Double> numbers) {
        Map<String, Object> payload = Map.of("numbers", numbers);
        return restTemplate.postForObject(mcpUrl + "/tools/calculateStats", payload, Map.class);
    }

    public double linearRegressionPredict(List<Double> xValues, List<Double> yValues, double xToPredict) {
        Map<String, Object> payload = Map.of(
                "xValues", xValues,
                "yValues", yValues,
                "xToPredict", xToPredict
        );
        return restTemplate.postForObject(mcpUrl + "/tools/linearRegressionPredict", payload, Double.class);
    }
}

