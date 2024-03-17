package ru.practicum.event.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ru.practicum.category.model.Category;
import ru.practicum.event.model.Event;

public interface EventRepository extends JpaRepository<Event, Long>, QuerydslPredicateExecutor<Event> {
    boolean existsByCategory(Category category);

    Page<Event> findByInitiatorId(Long userId, PageRequest of);
}
