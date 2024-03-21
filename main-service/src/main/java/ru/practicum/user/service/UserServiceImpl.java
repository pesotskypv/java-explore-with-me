package ru.practicum.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.exception.EntityConflictException;
import ru.practicum.exception.EntityNotFoundException;
import ru.practicum.subscription.dao.SubscriptionRepository;
import ru.practicum.user.dao.UserRepository;
import ru.practicum.user.dto.NewUserRequest;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.mapper.UserMapper;
import ru.practicum.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final SubscriptionRepository subscriptionRepository;

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
        if (userRepository.existsByEmail(newUserRequest.getEmail())) {
            throw new EntityConflictException("Нарушение целостности данных.");
        }
        User user = userMapper.toUser(newUserRequest);
        if (user.getSubscriptionBan() == null) {
            user.setSubscriptionBan(false);
        }
        UserDto userDto = userMapper.toUserDto(userRepository.save(user));

        log.info("Сохранён пользователь: {}", userDto);
        return userDto;
    }

    @Override
    public void deleteUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException("Пользователь не найден или недоступен.");
        }
        if (subscriptionRepository.existsByAuthorId(userId)) {
            subscriptionRepository.deleteByAuthorId(userId);
        }
        if (subscriptionRepository.existsBySubscriberId(userId)) {
            subscriptionRepository.deleteBySubscriberId(userId);
        }

        userRepository.deleteById(userId);
        log.info("Удалён пользователь с id: {}", userId);
    }

    @Override
    public UserDto updateUser(Long userId, UserDto userDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден или недоступен."));
        String name = userDto.getName();
        String email = userDto.getEmail();
        Boolean subscriptionBan = userDto.getSubscriptionBan();

        if (name != null) {
            user.setName(name);
        }
        if (email != null) {
            user.setEmail(email);
        }
        if (subscriptionBan != null) {
            user.setSubscriptionBan(subscriptionBan);
        }

        UserDto savedUser = userMapper.toUserDto(userRepository.save(user));

        log.info("Изменены параметры пользователя: {}", savedUser);
        return savedUser;
    }
}
