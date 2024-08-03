package com.assist.Internship_2024_java_yellow.services;


import com.assist.Internship_2024_java_yellow.dtos.ChatBotDTO;
import com.assist.Internship_2024_java_yellow.dtos.ResponseChatBot;

public interface ChatBotService {

    ResponseChatBot chatBot(ChatBotDTO chatBotDTO);
}
