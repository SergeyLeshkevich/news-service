package ru.clevertec.news.entity.dto;

import java.util.UUID;

public record UserRequest(

        UUID uuid,

        String userName) {
}
