package ru.practicum.request.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.request.model.ParticipationRequest;
import ru.practicum.request.model.ParticipationStatus;

import java.util.Collection;
import java.util.List;

public interface RequestRepository extends JpaRepository<ParticipationRequest, Long> {
    Long countByEventIdAndStatus(Long eventId, ParticipationStatus status);

    List<ParticipationRequest> findByEventId(Long eventId);

    List<ParticipationRequest> findByEventIdInAndStatus(Collection<Long> eventIds, ParticipationStatus status);

    List<ParticipationRequest> findByRequesterId(Long userId);

    boolean existsByRequesterIdAndEventId(Long userId, Long eventId);
}
