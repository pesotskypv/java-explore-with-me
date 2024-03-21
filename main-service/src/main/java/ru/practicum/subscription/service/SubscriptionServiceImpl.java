package ru.practicum.subscription.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.exception.EntityConflictException;
import ru.practicum.exception.EntityNotFoundException;
import ru.practicum.exception.EntityValidationException;
import ru.practicum.subscription.dao.SubscriptionRepository;
import ru.practicum.subscription.dto.SubscriptionDto;
import ru.practicum.subscription.mapper.SubscriptionMapper;
import ru.practicum.subscription.model.Subscription;
import ru.practicum.user.dao.UserRepository;
import ru.practicum.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubscriptionServiceImpl implements SubscriptionService {
    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionMapper subscriptionMapper;
    private final UserRepository userRepository;

    @Override
    public SubscriptionDto createSubscription(Long subscriberId, Long authorId) {
        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь (автор) не найден или недоступен."));
        User subscriber = userRepository.findById(subscriberId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь (подписчик) не найден или недоступен."));

        if (subscriberId.equals(authorId)) {
            throw new EntityValidationException("Нельзя подписаться на самого себя.");
        }
        if (subscriptionRepository.existsByAuthorIdAndSubscriberId(authorId, subscriberId)) {
            throw new EntityConflictException("Пользователь уже подписан на автора.");
        }
        if (author.getSubscriptionBan()) {
            throw new EntityNotFoundException("Пользователь запретил подписку на себя.");
        }

        SubscriptionDto subscriptionDto = subscriptionMapper.toSubscriptionDto(subscriptionRepository
                .save(Subscription.builder().author(author).subscriber(subscriber).created(LocalDateTime.now())
                        .build()));

        log.info("Сохранена подписка на пользователя: {}", subscriptionDto);
        return subscriptionDto;
    }

    @Override
    public void deleteSubscription(Long subscriberId, Long authorId) {
        if (!userRepository.existsById(authorId)) {
            throw new EntityNotFoundException("Пользователь-автор не найден или недоступен.");
        }
        if (!userRepository.existsById(subscriberId)) {
            throw new EntityNotFoundException("Пользователь-подписчик не найден или недоступен.");
        }

        Subscription subscription = subscriptionRepository.getByAuthorIdAndSubscriberId(authorId, subscriberId);

        if (subscription == null) {
            throw new EntityNotFoundException("Подписка не найдена или недоступна.");
        }

        subscriptionRepository.delete(subscription);
        log.info("Удалена подписка: {}", subscription);
    }

    @Override
    public List<SubscriptionDto> findSubscriptions(Long subscriberId, PageRequest of) {
        if (!userRepository.existsById(subscriberId)) {
            throw new EntityNotFoundException("Пользователь не найден или недоступен.");
        }

        List<SubscriptionDto> subscriptionDtos = subscriptionRepository.findBySubscriberId(subscriberId, of).stream()
                .map(subscriptionMapper::toSubscriptionDto).collect(Collectors.toList());

        log.info("Возвращён список подписок: {}", subscriptionDtos);
        return subscriptionDtos;
    }

    @Override
    public List<SubscriptionDto> findSubscribers(Long authorId, PageRequest of) {
        if (!userRepository.existsById(authorId)) {
            throw new EntityNotFoundException("Пользователь не найден или недоступен.");
        }

        List<SubscriptionDto> subscriptionDtos = subscriptionRepository.findByAuthorId(authorId, of).stream()
                .map(subscriptionMapper::toSubscriptionDto).collect(Collectors.toList());

        log.info("Возвращён список подписчиков: {}", subscriptionDtos);
        return subscriptionDtos;
    }
}
