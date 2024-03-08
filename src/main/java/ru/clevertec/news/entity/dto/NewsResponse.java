package ru.clevertec.news.entity.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import ru.clevertec.news.entity.User;

import java.io.Serializable;
import java.time.LocalDateTime;

public record NewsResponse(

        Long id,

        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
        LocalDateTime time,

        String title,

        String text,

        User user)  implements Serializable {
}
