package com.assist.Internship_2024_java_yellow.config;

import com.assist.Internship_2024_java_yellow.mappers.BidMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.mapstruct.factory.Mappers;

@Configuration
public class MapperConfig {
    @Bean
    public BidMapper bidMapper() {
        return Mappers.getMapper(BidMapper.class);
    }
}
