package com.miniprojet.smartdoc.agents;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.stereotype.Service;

@Service
public class AIAgent {

    private final ChatClient chatClient;

    public AIAgent(ChatClient.Builder chatClientBuilder,
                   ToolCallbackProvider toolCallbackProvider,
                   ChatMemory chatMemory) {

        this.chatClient = chatClientBuilder
                .defaultToolCallbacks(toolCallbackProvider)      
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                .defaultSystem("Vous êtes un assistant utile. Répondez toujours en français aux questions de l'utilisateur.")
                .build();
    }

    public String onQuery(String query) {
        if (query == null || query.trim().isEmpty()) {
            return "Veuillez fournir une question spécifique.";
        }

        String queryFR = "Réponds en français : " + query;

        return chatClient.prompt()
                .user(queryFR)
                .call()
                .content();
    }
}
