package com.assist.Internship_2024_java_yellow.controllers;

import com.assist.Internship_2024_java_yellow.dtos.ChatBotDTO;
import com.assist.Internship_2024_java_yellow.services.ChatBotService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/chat-bot")
@RequiredArgsConstructor
public class ChatBotController {

    private final ChatBotService chatBotService;

    @PostMapping()
    public ResponseEntity<?> chatBotRequest(@RequestBody ChatBotDTO chatBotDTO){
        return ResponseEntity.ok(chatBotService.chatBot(chatBotDTO));
    }

}
