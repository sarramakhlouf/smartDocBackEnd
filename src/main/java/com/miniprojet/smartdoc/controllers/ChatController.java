package com.miniprojet.smartdoc.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.*;

import com.miniprojet.smartdoc.services.RagService;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final RagService ragService;
    private final List<String> history = new ArrayList<>(); // stocke les messages

    public ChatController(RagService ragService) {
        this.ragService = ragService;
    }

    @PostMapping("/query")
    public String chat(@RequestBody Map<String, String> payload) {
        String question = payload.get("question"); // correspond au frontend
        String answer = ragService.query(question);

        history.add("User: " + question);
        history.add("Agent: " + answer);

        return answer;
    }


    @GetMapping("/history")
    public List<String> getHistory() {
        return history;
    }

    @PostMapping("/new")
    public void newConversation() {
        history.clear(); // effacer l'historique
    }
}
