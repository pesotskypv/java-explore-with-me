package ru.practicum.user.service;

import org.springframework.data.domain.PageRequest;
import ru.practicum.user.dto.NewUserRequest;
import ru.practicum.user.dto.UserDto;

import java.util.List;

public interface UserService {
    List<UserDto> findAllUsers(Long[] ids, PageRequest of);

    UserDto createUser(NewUserRequest newUserRequest);

    void deleteUser(Long userId);

    UserDto updateUser(Long userId, UserDto userDto);
}
