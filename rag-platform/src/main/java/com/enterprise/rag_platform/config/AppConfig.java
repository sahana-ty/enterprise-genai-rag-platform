package com.enterprise.rag_platform.config;

import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.List;

@Configuration
public class AppConfig {

    @Bean
    @Primary
    public ChatModel chatModel() {
        return new ChatModel() {
            @Override
            public ChatResponse call(Prompt prompt) {
                String syntheticAnswer = "Based on the retrieved enterprise document context, here is the synthesis: " +
                        "The system successfully matched your query against the stored Oracle database chunks.";
                return new ChatResponse(List.of(new Generation(new AssistantMessage(syntheticAnswer))));
            }

            @Override
            public reactor.core.publisher.Flux<ChatResponse> stream(Prompt prompt) {
                return reactor.core.publisher.Flux.just(call(prompt));
            }
        };
    }
}