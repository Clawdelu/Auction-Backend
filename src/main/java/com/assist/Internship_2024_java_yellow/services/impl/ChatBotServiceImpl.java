package com.assist.Internship_2024_java_yellow.services.impl;

import com.assist.Internship_2024_java_yellow.dtos.ChatBotDTO;
import com.assist.Internship_2024_java_yellow.dtos.ResponseChatBot;
import com.assist.Internship_2024_java_yellow.services.ChatBotService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class ChatBotServiceImpl implements ChatBotService {

    private static final String API_URL ="http://internship2024-ai-yellow.dev.assist.ro/question";

    private final RestTemplate restTemplate;

    @Override
    public ResponseChatBot chatBot(ChatBotDTO chatBotDTO) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");


        HttpEntity<ChatBotDTO> entity = new HttpEntity<>(chatBotDTO,headers);

        ResponseEntity<ResponseChatBot> response = restTemplate.exchange(API_URL, HttpMethod.POST, entity, ResponseChatBot.class);

        return response.getBody();
    }
}
