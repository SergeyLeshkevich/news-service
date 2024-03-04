package ru.clevertec.news.util;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.With;
import ru.clevertec.news.entity.dto.UserResponse;

@With
@AllArgsConstructor
@NoArgsConstructor(staticName = "aUserResponse")
public class UserResponseBuilder implements TestBuilder<UserResponse> {

    private String userName = "Test userName comment";

    @Override
    public UserResponse build() {
        return new UserResponse(userName);
    }
}
