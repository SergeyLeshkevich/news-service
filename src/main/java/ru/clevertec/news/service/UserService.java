package ru.clevertec.news.service;


import ru.clevertec.news.entity.User;
import ru.clevertec.news.entity.dto.UserRequest;

import java.util.Optional;
import java.util.UUID;

public interface UserService {
    User create(UserRequest userRequest);
    Optional<User> getByUuiD(UUID uuid);
}
