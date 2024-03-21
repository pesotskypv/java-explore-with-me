package ru.practicum.subscription.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.subscription.model.Subscription;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    boolean existsByAuthorId(Long userId);

    boolean existsBySubscriberId(Long userId);

    void deleteByAuthorId(Long userId);

    void deleteBySubscriberId(Long userId);

    boolean existsByAuthorIdAndSubscriberId(Long authorId, Long subscriberId);

    Subscription getByAuthorIdAndSubscriberId(Long authorId, Long subscriberId);

    Page<Subscription> findBySubscriberId(Long subscriberId, PageRequest of);

    Page<Subscription> findByAuthorId(Long authorId, PageRequest of);
}
