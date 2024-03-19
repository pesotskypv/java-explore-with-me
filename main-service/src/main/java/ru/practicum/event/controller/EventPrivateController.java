package ru.practicum.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventRequestStatusUpdateRequest;
import ru.practicum.event.dto.EventRequestStatusUpdateResult;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.dto.UpdateEventUserRequest;
import ru.practicum.event.service.EventService;
import ru.practicum.request.dto.ParticipationRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping("/users/{userId}/events")
@RequiredArgsConstructor
public class EventPrivateController {
    private final EventService eventService;

    @GetMapping
    public List<EventShortDto> findAllEventsByCurrentUser(@PathVariable @PositiveOrZero Long userId,
                                                          @RequestParam(required = false, defaultValue = "0")
                                                              @PositiveOrZero int from,
                                                          @RequestParam(required = false, defaultValue = "10")
                                                              @Positive int size) {
        log.info("Получен GET-запрос /users/{}/events?from={}&size={}", userId, from, size);
        return eventService.findAllEventsByCurrentUser(userId, PageRequest.of(from / size, size));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto createEvent(@PathVariable @PositiveOrZero Long userId,
                                    @RequestBody @Valid NewEventDto newEventDto) {
        log.info("Получен POST-запрос /users/{}/events с телом={}", userId, newEventDto);
        return eventService.createEvent(userId, newEventDto);
    }

    @GetMapping("/{eventId}")
    public EventFullDto getEventByCurrentUser(@PathVariable @PositiveOrZero Long userId,
                                              @PathVariable @PositiveOrZero Long eventId) {
        log.info("Получен GET-запрос /users/{}/events/{}", userId, eventId);
        return eventService.getEventByCurrentUser(userId, eventId);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto changeEventByCurrentUser(@PathVariable @PositiveOrZero Long userId,
                                                 @PathVariable @PositiveOrZero Long eventId,
                                                 @RequestBody @Valid UpdateEventUserRequest request) {
        log.info("Получен PATCH-запрос /users/{}/events/{} с телом={}", userId, eventId, request);
        return eventService.changeEventByCurrentUser(userId, eventId, request);
    }

    @GetMapping("/{eventId}/requests")
    public List<ParticipationRequestDto> findRequests(@PathVariable @PositiveOrZero Long userId,
                                                      @PathVariable @PositiveOrZero Long eventId) {
        log.info("Получен GET-запрос /users/{}/events/{}/requests", userId, eventId);
        return eventService.findRequests(userId, eventId);
    }

    @PatchMapping("/{eventId}/requests")
    public EventRequestStatusUpdateResult changeRequestStatus(@PathVariable @PositiveOrZero Long userId,
                                                              @PathVariable @PositiveOrZero Long eventId,
                                                              @RequestBody EventRequestStatusUpdateRequest request) {
        log.info("Получен PATCH-запрос /users/{}/events/{}/requests с телом={}", userId, eventId, request);
        return eventService.changeRequestStatus(userId, eventId, request);
    }
}
