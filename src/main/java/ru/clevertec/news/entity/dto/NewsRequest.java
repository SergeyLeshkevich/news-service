package ru.clevertec.news.entity.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record NewsRequest(

        @NotBlank
        @Size(min = 1, max = 50)
        String title,

        @NotBlank
        @Size(min = 1, max = 2000)
        String text,

        @NotNull
        UserRequest user) {
}
