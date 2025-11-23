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
    private final List<String> history = new ArrayList<>(); // historique des messages

    public ChatController(RagService ragService) {
        this.ragService = ragService;
    }

    // ------------------- Poser une question -------------------
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

    // ------------------- Récupérer l'historique -------------------
    @GetMapping("/history")
    public ResponseEntity<List<String>> getHistory() {
        return ResponseEntity.ok(history);
    }

    // ------------------- Nouvelle conversation -------------------
    @PostMapping("/new")
    public ResponseEntity<Void> newConversation() {
        history.clear(); // effacer l'historique
        return ResponseEntity.ok().build();
    }
}
