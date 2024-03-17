package ru.practicum.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.event.dao.EventRepository;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.EventState;
import ru.practicum.exception.EntityConflictException;
import ru.practicum.exception.EntityNotFoundException;
import ru.practicum.request.dao.RequestRepository;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.mapper.RequestMapper;
import ru.practicum.request.model.ParticipationRequest;
import ru.practicum.request.model.ParticipationStatus;
import ru.practicum.user.dao.UserRepository;
import ru.practicum.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final RequestMapper requestMapper;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Override
    public List<ParticipationRequestDto> getRequests(Long userId) {
        if (!userRepository.existsById(userId))
            throw new EntityNotFoundException("Пользователь не найден или недоступен.");
        List<ParticipationRequestDto> participationRequestDtos = requestRepository.findByRequesterId(userId)
                .stream().map(requestMapper::toParticipationRequestDto).collect(Collectors.toList());

        log.info("Возвращён список запросов на участие в событии текущего пользователя: {}", participationRequestDtos);
        return participationRequestDtos;
    }

    @Override
    public ParticipationRequestDto createRequest(Long userId, Long eventId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден или недоступен."));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Событие не найдено или недоступно."));
        Integer limit = event.getParticipantLimit();
        Long countRequests = requestRepository.countConfirmedRequestsByEventId(eventId);

        if (requestRepository.existsByRequesterIdAndEventId(userId, eventId))
            throw new EntityConflictException("Нельзя добавить повторный запрос на участие в событии.");
        if (event.getInitiator().equals(user))
            throw new EntityConflictException("Инициатор события не может добавить запрос на участие в своём событии.");
        if (!event.getState().equals(EventState.PUBLISHED))
            throw new EntityConflictException("Нельзя участвовать в неопубликованном событии.");
        if (limit != null && limit != 0 && countRequests >= limit)
            throw new EntityConflictException("У события достигнут лимит запросов на участие в событии.");

        ParticipationStatus status = event.getRequestModeration()
                ? ParticipationStatus.PENDING : ParticipationStatus.CONFIRMED;

        ParticipationRequestDto request = requestMapper.toParticipationRequestDto(requestRepository
                .save(ParticipationRequest.builder()
                        .requester(user).event(event).created(LocalDateTime.now())
                        .status(limit != null && limit == 0 ? ParticipationStatus.CONFIRMED : status)
                        .build()));

        log.info("Создан запрос на участие в событии: {}", request);
        return request;
    }

    @Override
    public ParticipationRequestDto cancelRequest(Long userId, Long requestId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден или недоступен."));
        ParticipationRequest participationRequest = requestRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException("Запрос на участие не найден или недоступен."));
        if (!participationRequest.getRequester().equals(user))
            throw new EntityNotFoundException("Текущий пользователь не является автором запрос на участие.");

        participationRequest.setStatus(ParticipationStatus.CANCELED);

        ParticipationRequestDto participationRequestDto =
                requestMapper.toParticipationRequestDto(requestRepository.save(participationRequest));
        log.info("Запрос на участие в событии отменён: {}", participationRequestDto);

        return participationRequestDto;
    }
}
