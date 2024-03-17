package ru.practicum.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.exception.EntityConflictException;
import ru.practicum.exception.EntityNotFoundException;
import ru.practicum.user.dao.UserRepository;
import ru.practicum.user.dto.NewUserRequest;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.mapper.UserMapper;
import ru.practicum.user.model.User;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public List<UserDto> findAllUsers(Long[] ids, PageRequest of) {
        List<UserDto> userDtos = (ids != null) ? userRepository.findByIdIn(List.of(ids), of).stream()
                .map(userMapper::toUserDto).collect(Collectors.toList()) : userRepository.findAll(of).stream()
                .map(userMapper::toUserDto).collect(Collectors.toList());

        log.info("Возвращён список пользователей: {}", userDtos);

        return userDtos;
    }

    @Override
    public UserDto createUser(NewUserRequest newUserRequest) {
        if (userRepository.findByEmail(newUserRequest.getEmail()) != null)
            throw new EntityConflictException("Нарушение целостности данных");

        UserDto userDto = userMapper.toUserDto(userRepository.save(userMapper.toUser(newUserRequest)));

        log.info("Сохранён пользователь: {}", userDto);
        return userDto;
    }

    @Override
    public void deleteUser(Long userId) {
        if (!userRepository.existsById(userId))
            throw new EntityNotFoundException("Пользователь не найден или недоступен");
        userRepository.deleteById(userId);
        log.info("Удалён пользователь с id: {}", userId);
    }
}
