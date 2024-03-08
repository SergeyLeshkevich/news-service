package ru.clevertec.news.entity.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record UserRequest(

        @NotNull
        UUID uuid,

        @NotNull
        String userName) {
}
