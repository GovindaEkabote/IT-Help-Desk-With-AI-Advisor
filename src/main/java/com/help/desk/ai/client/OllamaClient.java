package com.help.desk.ai.client;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import tools.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
@RequiredArgsConstructor
public class OllamaClient {

    private final WebClient.Builder webClientBuilder;
    private final ObjectMapper objectMapper;

    @Value("${spring.ai.ollama.base-url:http://localhost:11434}")
    private String baseUrl;

    @Value("${spring.ai.ollama.model:llama3}")
    private String model;

    private WebClient webClient;

    private WebClient getWebClient() {
        if (webClient == null) {
            webClient = webClientBuilder.baseUrl(baseUrl).build();
        }
        return webClient;
    }

    /**
     * Send prompt to Ollama and get response
     */
    public String generateResponse(String prompt) {
        try {
            Map<String, Object> request = new HashMap<>();
            request.put("model", model);
            request.put("prompt", prompt);
            request.put("stream", false);

            Map<String, Object> options = new HashMap<>();
            options.put("temperature", 0.7);
            options.put("top_p", 0.9);
            request.put("options", options);

            String response = getWebClient()
                    .post()
                    .uri("/api/generate")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            if (response != null) {
                Map<String, Object> responseMap = objectMapper.readValue(response, Map.class);
                Object responseObj = responseMap.get("response");
                return responseObj != null ? responseObj.toString() : "No response from AI";
            }
            return "Unable to get response from AI";

        } catch (Exception e) {
            log.error("Error calling Ollama: {}", e.getMessage());
            return null;
        }
    }
}