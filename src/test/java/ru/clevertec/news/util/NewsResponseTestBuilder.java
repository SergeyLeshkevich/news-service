package ru.clevertec.news.util;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.With;
import ru.clevertec.news.entity.User;
import ru.clevertec.news.entity.dto.NewsResponse;

import java.time.LocalDateTime;

@With
@AllArgsConstructor
@NoArgsConstructor(staticName = "aNewsResponse")
public class NewsResponseTestBuilder implements TestBuilder<NewsResponse> {

    private Long id = 1L;
    private LocalDateTime time = LocalDateTime.parse("2024-01-01T14:18:08.537000");
    private String title = "Test news title";
    private String text = "Test news text";
    private User user = UserTestBuilder.aUser().build();

    @Override
    public NewsResponse build() {
        return new NewsResponse(id, time, title, text, user);
    }
}
