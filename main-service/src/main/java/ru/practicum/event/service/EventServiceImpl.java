package ru.practicum.event.service;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.category.dao.CategoryRepository;
import ru.practicum.category.model.Category;
import ru.practicum.event.dao.EventRepository;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventRequestStatus;
import ru.practicum.event.dto.EventRequestStatusUpdateRequest;
import ru.practicum.event.dto.EventRequestStatusUpdateResult;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.dto.StateAction;
import ru.practicum.event.dto.StateActionAdmin;
import ru.practicum.event.dto.UpdateEvent;
import ru.practicum.event.dto.UpdateEventAdminRequest;
import ru.practicum.event.dto.UpdateEventUserRequest;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.EventState;
import ru.practicum.event.model.QEvent;
import ru.practicum.exception.EntityConflictException;
import ru.practicum.exception.EntityNotFoundException;
import ru.practicum.exception.EntityValidationException;
import ru.practicum.model.EndpointHitDto;
import ru.practicum.model.EndpointHitDtoReq;
import ru.practicum.model.ViewStatsDto;
import ru.practicum.request.dao.RequestRepository;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.mapper.RequestMapper;
import ru.practicum.request.model.ParticipationRequest;
import ru.practicum.request.model.ParticipationStatus;
import ru.practicum.user.dao.UserRepository;
import ru.practicum.user.model.User;
import ru.practicum.service.StatsClientService;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final RequestRepository requestRepository;
    private final RequestMapper requestMapper;
    private final StatsClientService statsClientService;

    @Override
    public List<EventShortDto> findAllEventsByCurrentUser(Long userId, PageRequest of) {
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException("Пользователь не найден или недоступен");
        }

        List<EventShortDto> eventShortDtos = eventRepository.findByInitiatorId(userId, of).stream()
                .map(eventMapper::toEventShortDto).collect(Collectors.toList());
        List<ParticipationRequest> participationRequests = requestRepository.findByEventIdInAndStatus(eventShortDtos
                .stream().map(EventShortDto::getId).collect(Collectors.toList()), ParticipationStatus.CONFIRMED);
        eventShortDtos = eventShortDtos.stream()
                .peek(e -> e.setConfirmedRequests(participationRequests.stream()
                        .filter(r -> r.getEvent().getId().equals(e.getId())).count()))
                .peek(e -> e.setViews(getEventViews(e.getId())))
                .collect(Collectors.toList());

        log.info("Возвращён список событий: {}", eventShortDtos);
        return eventShortDtos;
    }

    @Override
    public EventFullDto createEvent(Long userId, NewEventDto newEventDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден или недоступен."));
        Category category = categoryRepository.findById(newEventDto.getCategory())
                .orElseThrow(() -> new EntityNotFoundException("Категория не найдена или недоступна."));

        eventDateValidation(newEventDto.getEventDate());

        Boolean paid = newEventDto.getPaid();
        Integer limit = newEventDto.getParticipantLimit();
        Boolean requestModeration = newEventDto.getRequestModeration();
        Event event = eventMapper.toEvent(newEventDto, user, category);

        event.setCreatedOn(LocalDateTime.now());
        event.setPaid(paid != null ? paid : false);
        event.setParticipantLimit(limit != null ? limit : 0);
        event.setRequestModeration(requestModeration != null ? requestModeration : true);
        event.setState(EventState.PENDING);

        EventFullDto eventFullDto = eventMapper.toEventFullDto(eventRepository.save(event));

        log.info("Добавлено событие: {}", eventFullDto);
        return eventFullDto;
    }

    @Override
    public EventFullDto getEventByCurrentUser(Long userId, Long eventId) {
        EventFullDto eventFullDto = eventMapper.toEventFullDto(eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Событие не найдено или недоступно.")));

        if (!eventFullDto.getInitiator().getId().equals(userId)) {
            throw new EntityNotFoundException("Текущий пользователь не является автором события.");
        }

        eventFullDto.setConfirmedRequests(requestRepository.countByEventIdAndStatus(eventId,
                ParticipationStatus.CONFIRMED));
        eventFullDto.setViews(getEventViews(eventId));

        log.info("Возвращено событие: {}", eventFullDto);
        return eventFullDto;
    }

    @Override
    public EventFullDto changeEventByCurrentUser(Long userId, Long eventId, UpdateEventUserRequest request) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Событие не найдено или недоступно."));

        if (!event.getInitiator().getId().equals(userId)) {
            throw new EntityNotFoundException("Текущий пользователь не является автором события.");
        }
        if (event.getState().equals(EventState.PUBLISHED)) {
            throw new EntityConflictException("Изменить можно только отменённые события или события в состоянии" +
                    "ожидания модерации.");
        }

        LocalDateTime eventDate = request.getEventDate();
        StateAction state = request.getStateAction();

        if (eventDate != null) {
            eventDateValidation(eventDate);
            event.setEventDate(eventDate);
        }
        if (state != null) {
            event.setState(state.equals(StateAction.SEND_TO_REVIEW) ? EventState.PENDING : EventState.CANCELED);
        }

        EventFullDto eventFullDto = eventMapper.toEventFullDto(eventRepository
                .save(eventValidateAndSetFields(event, eventMapper.updateEventUserRequestToUpdateEvent(request))));

        eventFullDto.setConfirmedRequests(requestRepository.countByEventIdAndStatus(eventId,
                ParticipationStatus.CONFIRMED));
        eventFullDto.setViews(getEventViews(eventId));

        log.info("Пользователем изменено событие: {}", eventFullDto);
        return eventFullDto;
    }

    @Override
    public List<ParticipationRequestDto> findRequests(Long userId, Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Событие не найдено или недоступно."));

        if (!event.getInitiator().getId().equals(userId)) {
            throw new EntityNotFoundException("Текущий пользователь не является автором события.");
        }

        List<ParticipationRequestDto> participationRequestDtos = requestRepository.findByEventId(eventId).stream()
                .map(requestMapper::toParticipationRequestDto).collect(Collectors.toList());

        log.info("Возвращён список запросов на участие в событии текущего пользователя: {}", participationRequestDtos);
        return participationRequestDtos;
    }

    @Override
    public EventRequestStatusUpdateResult changeRequestStatus(Long userId, Long eventId,
                                                              EventRequestStatusUpdateRequest request) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Событие не найдено или недоступно."));

        if (!event.getInitiator().getId().equals(userId)) {
            throw new EntityNotFoundException("Текущий пользователь не является автором события.");
        }

        AtomicReference<Long> countRequests = new AtomicReference<>(requestRepository.countByEventIdAndStatus(eventId,
                ParticipationStatus.CONFIRMED));
        Integer participantLimit = event.getParticipantLimit();
        boolean eventRequestStatus = request.getStatus().equals(EventRequestStatus.CONFIRMED);

        if (eventRequestStatus && participantLimit != null && participantLimit != 0
                && event.getRequestModeration().equals(true) && countRequests.get() >= participantLimit) {
            throw new EntityConflictException("Достигнут лимит по заявкам на данное событие.");
        }

        List<ParticipationRequest> requests = requestRepository.findAllById(Arrays.asList(request.getRequestIds()));

        requests.forEach(r -> {
            if (!r.getStatus().equals(ParticipationStatus.PENDING)) {
                throw new EntityConflictException("Статус можно изменить только у заявок, находящихся в состоянии " +
                        "ожидания");
            }
        });

        EventRequestStatusUpdateResult result = EventRequestStatusUpdateResult.builder().build();
        if (participantLimit != null && participantLimit != 0 && event.getRequestModeration()) {
            List<ParticipationRequest> changedRequests = requests.stream().peek(r -> {
                if (eventRequestStatus) {
                    if (countRequests.get() < participantLimit) {
                        r.setStatus(ParticipationStatus.CONFIRMED);
                    } else {
                        r.setStatus(ParticipationStatus.REJECTED);
                    }
                    countRequests.getAndSet(countRequests.get() + 1);
                } else {
                    r.setStatus(ParticipationStatus.REJECTED);
                }
                requestRepository.save(r);
            }).collect(Collectors.toList());

            result.setConfirmedRequests(changedRequests.stream()
                    .filter(r -> r.getStatus().equals(ParticipationStatus.CONFIRMED))
                    .map(requestMapper::toParticipationRequestDto).collect(Collectors.toList()));
            result.setRejectedRequests(changedRequests.stream()
                    .filter(r -> r.getStatus().equals(ParticipationStatus.REJECTED))
                    .map(requestMapper::toParticipationRequestDto).collect(Collectors.toList()));
            log.info("Изменены статусы заявок на участие в событии текущего пользователя: {}", result);
        } else {
            result.setConfirmedRequests(Collections.emptyList());
            result.setRejectedRequests(Collections.emptyList());
            log.info("Подтверждение заявок не требуется: {}", result);
        }
        return result;
    }

    @Override
    public List<EventFullDto> findAllEvents(Long[] users, String[] states, Long[] categories, LocalDateTime rangeStart,
                                            LocalDateTime rangeEnd, PageRequest of) {
        BooleanBuilder builder = new BooleanBuilder();
        if (Objects.nonNull(users)) {
            BooleanExpression byUserId = QEvent.event.initiator.id.in(users);
            builder.and(byUserId);
        }
        if (Objects.nonNull(states)) {
            List<EventState> eventStates = Arrays.stream(states).map(EventState::valueOf).collect(Collectors.toList());
            BooleanExpression byStateId = QEvent.event.state.in(eventStates);
            builder.and(byStateId);
        }
        if (Objects.nonNull(categories)) {
            BooleanExpression byCategoryId = QEvent.event.category.id.in(categories);
            builder.and(byCategoryId);
        }
        if (Objects.nonNull(rangeStart) || Objects.nonNull(rangeEnd)) {
            BooleanExpression byDate = QEvent.event.eventDate.between(rangeStart, rangeEnd);
            builder.and(byDate);
        }

        List<EventFullDto> eventFullDtos = eventRepository.findAll(builder, of).getContent().stream()
                .map(eventMapper::toEventFullDto).collect(Collectors.toList());
        List<ParticipationRequest> participationRequests = requestRepository.findByEventIdInAndStatus(eventFullDtos
                .stream().map(EventFullDto::getId).collect(Collectors.toList()), ParticipationStatus.CONFIRMED);
        eventFullDtos = eventFullDtos.stream()
                .peek(e -> e.setConfirmedRequests(participationRequests.stream()
                        .filter(r -> r.getEvent().getId().equals(e.getId())).count()))
                .peek(e -> e.setViews(getEventViews(e.getId())))
                .collect(Collectors.toList());

        log.info("Администратору возвращён список событий: {}", eventFullDtos);
        return eventFullDtos;
    }

    @Override
    public EventFullDto changeEventByAdmin(Long eventId, UpdateEventAdminRequest request) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Событие не найдено или недоступно."));
        EventState eventState = event.getState();
        StateActionAdmin requestState = request.getStateAction();
        LocalDateTime requestEventDate = request.getEventDate();

        if (requestState != null) {
            if (requestState.equals(StateActionAdmin.PUBLISH_EVENT)) {
                if (eventState.equals(EventState.PENDING)) {
                    event.setState(EventState.PUBLISHED);
                } else {
                    throw new EntityConflictException("Событие можно публиковать, только если оно в состоянии " +
                            "ожидания публикации.");
                }
            } else {
                if (!eventState.equals(EventState.PUBLISHED)) {
                    event.setState(EventState.CANCELED);
                } else {
                    throw new EntityConflictException("Событие можно отклонить, только если оно ещё не опубликовано.");
                }
            }
        }
        if (requestEventDate != null) {
            if (requestEventDate.isBefore(LocalDateTime.now().plusHours(1))) {
                throw new EntityValidationException("Дата начала изменяемого события должна быть не ранее чем за час " +
                        "от даты публикации.");
            }
            event.setEventDate(requestEventDate);
        }

        EventFullDto eventFullDto = eventMapper.toEventFullDto(eventRepository
                .save(eventValidateAndSetFields(event, eventMapper.updateEventAdminRequestToUpdateEvent(request))));

        eventFullDto.setConfirmedRequests(requestRepository.countByEventIdAndStatus(eventId,
                ParticipationStatus.CONFIRMED));
        eventFullDto.setViews(getEventViews(eventId));

        log.info("Изменено администратором событие: {}", eventFullDto);
        return eventFullDto;
    }

    @Override
    public List<EventShortDto> findAllEventsByParams(String text, Long[] categories, Boolean paid,
                                                     LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                                     Boolean onlyAvailable, String sort, PageRequest of,
                                                     HttpServletRequest request) {
        BooleanBuilder builder = new BooleanBuilder();
        BooleanExpression byDate;

        if (Objects.nonNull(text)) {
            BooleanExpression byAnnotation = QEvent.event.annotation.containsIgnoreCase(text);
            BooleanExpression byDescription = QEvent.event.description.containsIgnoreCase(text);
            builder.and(byAnnotation).or(byDescription);
        }
        if (Objects.nonNull(categories)) {
            BooleanExpression byCategoryId = QEvent.event.category.id.in(categories);
            builder.and(byCategoryId);
        }
        if (Objects.nonNull(paid)) {
            BooleanExpression byPaid = QEvent.event.paid.eq(paid);
            builder.and(byPaid);
        }
        if (Objects.nonNull(rangeStart) && Objects.nonNull(rangeEnd)) {
            if (rangeStart.isAfter(rangeEnd)) {
                throw new EntityValidationException("Дата начала периода не может быть позже даты конца периода.");
            }
            byDate = QEvent.event.eventDate.between(rangeStart, rangeEnd);
        } else {
            byDate = QEvent.event.eventDate.after(LocalDateTime.now());
        }
        builder.and(byDate);

        List<EventShortDto> eventShortDtos = eventRepository.findAll(builder, of).stream()
                .map(eventMapper::toEventShortDto).collect(Collectors.toList());
        List<ParticipationRequest> participationRequests = requestRepository.findByEventIdInAndStatus(eventShortDtos
                .stream().map(EventShortDto::getId).collect(Collectors.toList()), ParticipationStatus.CONFIRMED);
        eventShortDtos = eventShortDtos.stream()
                .peek(e -> e.setConfirmedRequests(participationRequests.stream()
                        .filter(r -> r.getEvent().getId().equals(e.getId())).count()))
                .peek(e -> e.setViews(getEventViews(e.getId())))
                .collect(Collectors.toList());

        if (sort != null) {
            if (sort.equals("EVENT_DATE")) {
                eventShortDtos = eventShortDtos.stream().sorted(Comparator.comparing(EventShortDto::getEventDate))
                        .collect(Collectors.toList());
            }
            if (sort.equals("VIEWS")) {
                eventShortDtos = eventShortDtos.stream().sorted(Comparator.comparing(EventShortDto::getViews))
                        .collect(Collectors.toList());
            }
        }
        addStat(request);

        log.info("Пользователю возвращён список событий: {}", eventShortDtos);
        return eventShortDtos;
    }

    @Override
    public EventFullDto getEventById(Long eventId, HttpServletRequest request) {
        EventFullDto eventFullDto = eventMapper.toEventFullDto(eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Событие не найдено или недоступно.")));

        if (!eventFullDto.getState().equals(EventState.PUBLISHED)) {
            throw new EntityNotFoundException("Событие не опубликовано.");
        }

        eventFullDto.setConfirmedRequests(requestRepository.countByEventIdAndStatus(eventId,
                ParticipationStatus.CONFIRMED));
        eventFullDto.setViews(getEventViews(eventId));
        addStat(request);

        log.info("Пользователю возвращёно событие: {}", eventFullDto);
        return eventFullDto;
    }

    private void eventDateValidation(LocalDateTime dateTime) {
        if (dateTime.isBefore(LocalDateTime.now().plusHours(2))) {
            throw new EntityValidationException("Дата и время на которые намечено событие не может быть раньше, " +
                    "чем через два часа от текущего момента.");
        }
    }

    private Event eventValidateAndSetFields(Event event, UpdateEvent updateEvent) {
        Long categoryId = updateEvent.getCategory();

        if (categoryId != null) {
            Category category = categoryRepository.findById(categoryId).orElseThrow(() ->
                    new EntityNotFoundException("Категория не найдена или недоступна."));
            event.setCategory(category);
        }
        String annotation = updateEvent.getAnnotation();
        String description = updateEvent.getDescription();
        Boolean paid = updateEvent.getPaid();
        Integer participantLimit = updateEvent.getParticipantLimit();
        Boolean requestModeration = updateEvent.getRequestModeration();
        String title = updateEvent.getTitle();

        if (annotation != null) {
            event.setAnnotation(annotation);
        }
        if (description != null) {
            event.setDescription(description);
        }
        if (paid != null) {
            event.setPaid(paid);
        }
        if (participantLimit != null) {
            event.setParticipantLimit(participantLimit);
        }
        if (requestModeration != null) {
            event.setRequestModeration(requestModeration);
        }
        if (title != null) {
            event.setTitle(title);
        }

        return event;
    }

    private Long getEventViews(Long eventId) {
        List<ViewStatsDto> viewStatsDtos = statsClientService.findStats(LocalDateTime.now().minusYears(1),
                LocalDateTime.now().plusDays(1), new String[]{"/events/" + eventId},true);
        Long countViewStats = (long) viewStatsDtos.size();

        log.info("Возвращено количество просмотров из сервиса статистики: {}", countViewStats);
        return countViewStats;
    }

    private void addStat(HttpServletRequest request) {
        EndpointHitDto endpointHitDto = statsClientService.addStat(EndpointHitDtoReq.builder()
                .app("main-service").uri(request.getRequestURI()).ip(request.getRemoteAddr())
                .timestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).build());
        log.info("Выполнено сохранение в сервисе статистики: {}", endpointHitDto);
    }
}
