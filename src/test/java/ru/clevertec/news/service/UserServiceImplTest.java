package ru.clevertec.news.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.clevertec.news.entity.User;
import ru.clevertec.news.entity.dto.UserRequest;
import ru.clevertec.news.mapper.UserMapper;
import ru.clevertec.news.repository.UserRepository;
import ru.clevertec.news.util.UserRequestBuilder;
import ru.clevertec.news.util.UserTestBuilder;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;


    @Test
    void shouldCreateUserSuccessfully() {
        // given
        UserRequest userRequest = UserRequestBuilder.aUserRequest().build();
        User user = UserTestBuilder.aUser().build();
        when(userMapper.toEntity(userRequest)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);

        // when
        User result = userService.create(userRequest);

        // then
        assertThat(result).isNotNull();
        verify(userRepository).save(user);
    }

    @Test
    void shouldFindUserByUuidSuccessfully() {
        // given
        UUID uuid = UUID.randomUUID();
        Optional<User> expectedUser = Optional.of(UserTestBuilder.aUser().build());
        when(userRepository.findByUuid(uuid)).thenReturn(expectedUser);

        // when
        Optional<User> result = userService.getByUuiD(uuid);

        // then
        assertThat(result).isPresent();
        assertEquals(expectedUser, result);
    }

    @Test
    void shouldReturnEmptyWhenUserNotFoundByUuid() {
        // given
        UUID uuid = UUID.randomUUID();
        when(userRepository.findByUuid(uuid)).thenReturn(Optional.empty());

        // when
        Optional<User> result = userService.getByUuiD(uuid);

        // then
        assertThat(result).isEmpty();
    }
}
