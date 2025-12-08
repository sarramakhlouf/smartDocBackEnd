package com.miniprojet.smartdoc.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.miniprojet.smartdoc.services.RagService;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final RagService ragService;
    private final List<String> history = new ArrayList<>();

    public ChatController(RagService ragService) {
        this.ragService = ragService;
    }

    @PostMapping("/query")
    public ResponseEntity<String> chat(@RequestBody Map<String, String> payload) {
        String question = payload.get("question");

        if (question == null || question.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Veuillez fournir une question valide.");
        }

        String answer = ragService.query(question);

        history.add("User: " + question);
        history.add("Agent: " + answer);

        return ResponseEntity.ok(answer);
    }

    @GetMapping("/history")
    public ResponseEntity<List<String>> getHistory() {
        return ResponseEntity.ok(history);
    }

    @PostMapping("/new")
    public ResponseEntity<Void> newConversation() {
        history.clear();
        return ResponseEntity.ok().build();
    }
}
