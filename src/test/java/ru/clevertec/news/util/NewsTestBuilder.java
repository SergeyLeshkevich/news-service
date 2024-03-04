package ru.clevertec.news.util;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.With;
import ru.clevertec.news.entity.News;
import ru.clevertec.news.entity.User;

import java.time.LocalDateTime;

@With
@AllArgsConstructor
@NoArgsConstructor(staticName = "aNews")
public class NewsTestBuilder implements TestBuilder<News> {

    private Long id = 1L;
    private LocalDateTime time = LocalDateTime.MIN;
    private String title = "Test news title";
    private String text = "Test news text";
    private User user = UserTestBuilder.aUser().build();
    private boolean isArchived = false;

    @Override
    public News build() {
        return new News(id, time, title, text, user, isArchived);
    }
}
