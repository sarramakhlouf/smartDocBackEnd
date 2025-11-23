package com.miniprojet.smartdoc.controllers;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.miniprojet.smartdoc.services.RagService;

@RestController
@RequestMapping("/api/stats")
public class StatsController {

    private final RagService ragService;

    public StatsController(RagService ragService) {
        this.ragService = ragService;
    }

    @PostMapping("/text")
    public ResponseEntity<Map<String, Double>> statsFromText(@RequestBody Map<String, String> payload) {
        String text = payload.get("text");
        if (text == null || text.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        Map<String, Double> stats = ragService.getStatsFromText(text);
        return ResponseEntity.ok(stats);
    }

    @PostMapping("/numbers")
    public ResponseEntity<Map<String, Double>> statsFromNumbers(@RequestBody Map<String, List<Double>> payload) {
        List<Double> numbers = payload.get("numbers");
        if (numbers == null || numbers.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        Map<String, Double> stats = ragService.getStatsFromNumbers(numbers);
        return ResponseEntity.ok(stats);
    }
}
