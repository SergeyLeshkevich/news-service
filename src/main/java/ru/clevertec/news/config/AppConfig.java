package ru.clevertec.news.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.clevertec.exceptionhandlerstarter.handler.NewsManagementSystemExceptionHandler;

@Configuration
public class AppConfig {

    @Bean
    public NewsManagementSystemExceptionHandler handler(){
        return new NewsManagementSystemExceptionHandler();
    }
}
