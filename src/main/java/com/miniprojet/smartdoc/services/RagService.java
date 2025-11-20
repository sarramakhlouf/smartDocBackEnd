package com.miniprojet.smartdoc.services;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

import com.miniprojet.smartdoc.agents.AIAgent;

@Service
public class RagService {
	 private final AIAgent aiAgent;
	private final Map<String, String> vectorStore = new ConcurrentHashMap<>(); // simple mémoire

    public RagService(AIAgent aiAgent) {
        this.aiAgent = aiAgent;
    }

    public void indexDocument(String docId, String text) {
        // Stocker le texte pour RAG
        vectorStore.put(docId, text);
    }

    public String query(String question) {
        // Récupérer les contextes les plus proches (très simple : retourne tout)
        StringBuilder context = new StringBuilder();
        vectorStore.values().forEach(t -> context.append(t).append("\n---\n"));

        String prompt = "Answer the user question using the context below. If unknown, say you don't know.\nCONTEXT:\n"
                        + context + "\nQUESTION:\n" + question;

        return aiAgent.onQuery(prompt);
    }

}
