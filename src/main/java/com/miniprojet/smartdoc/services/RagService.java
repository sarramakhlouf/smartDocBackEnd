package com.miniprojet.smartdoc.services;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.miniprojet.smartdoc.agents.AIAgent;

import java.util.List;
import java.util.Map;

@Service
public class RagService {

    private final AIAgent aiAgent;
    private final RestTemplate restTemplate;
    private final StatsService statsService;

    private final String faissUrl = "http://localhost:5000";

    public RagService(AIAgent aiAgent, RestTemplate restTemplate, StatsService statsService) {
        this.aiAgent = aiAgent;
        this.restTemplate = restTemplate;
        this.statsService = statsService;
    }

    public void indexDocument(String docId, String text) {
        restTemplate.postForObject(faissUrl + "/add", Map.of("text", text), Map.class);
    }

    public String query(String question) {
        Map<String, String> payload = Map.of("query", question);
        Map<String, Object> response = restTemplate.postForObject(faissUrl + "/search", payload, Map.class);
        List<String> results = (List<String>) response.get("results");

        StringBuilder context = new StringBuilder();
        results.forEach(t -> context.append(t).append("\n---\n"));

        String prompt = "Réponds en français à la question en utilisant le contexte ci-dessous. Si tu ne sais pas, dis le.\n\nCONTEXT:\n"
                        + context + "\nQUESTION:\n" + question;

        return aiAgent.onQuery(prompt);
    }

    public Map<String, Double> getStatsFromText(String text) {
        String[] sentences = text.split("\\.\\s+");
        List<Double> lengths = java.util.Arrays.stream(sentences)
                                    .map(String::length)
                                    .map(Double::valueOf)
                                    .toList();

        return statsService.calculateStats(lengths);
    }
    
    public Map<String, Double> getStatsFromNumbers(List<Double> numbers) {
        return statsService.calculateStats(numbers);
    }
}
