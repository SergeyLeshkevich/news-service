package ru.clevertec.news.util;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.With;
import ru.clevertec.news.entity.dto.NewsRequest;
import ru.clevertec.news.entity.dto.UserRequest;

@With
@AllArgsConstructor
@NoArgsConstructor(staticName = "aNewsRequest")
public class NewsRequestTestBuilder implements TestBuilder<NewsRequest> {

    private String title = "Test news title";
    private String text = "Test news text";
    private UserRequest user = UserRequestBuilder.aUserRequest().build();

    @Override
    public NewsRequest build() {
        return new NewsRequest(title, text, user);
    }
}
