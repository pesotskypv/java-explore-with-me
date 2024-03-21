package ru.practicum.subscription.service;

import org.springframework.data.domain.PageRequest;
import ru.practicum.subscription.dto.SubscriptionDto;

import java.util.List;

public interface SubscriptionService {
    SubscriptionDto createSubscription(Long subscriberId, Long authorId);

    void deleteSubscription(Long subscriberId, Long authorId);

    List<SubscriptionDto> findSubscriptions(Long subscriberId, PageRequest of);

    List<SubscriptionDto> findSubscribers(Long authorId, PageRequest of);
}
