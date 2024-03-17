package ru.practicum.event.service;

import org.springframework.data.domain.PageRequest;
import ru.practicum.event.dto.*;
import ru.practicum.request.dto.ParticipationRequestDto;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

public interface EventService {
    List<EventShortDto> findAllEventsByCurrentUser(Long userId, PageRequest of);

    EventFullDto createEvent(Long userId, NewEventDto newEventDto);

    EventFullDto getEventByCurrentUser(Long userId, Long eventId);

    EventFullDto changeEventByCurrentUser(Long userId, Long eventId, UpdateEventUserRequest request);

    List<ParticipationRequestDto> findRequests(Long userId, Long eventId);

    EventRequestStatusUpdateResult changeRequestStatus(Long userId, Long eventId,
                                                       EventRequestStatusUpdateRequest request);

    List<EventFullDto> findAllEvents(Long[] users, String[] states, Long[] categories, LocalDateTime rangeStart,
                                     LocalDateTime rangeEnd, PageRequest of);

    EventFullDto changeEventByAdmin(Long eventId, UpdateEventAdminRequest request);

    List<EventShortDto> findAllEventsByParams(String text, Long[] categories, Boolean paid, LocalDateTime rangeStart,
                                              LocalDateTime rangeEnd, Boolean onlyAvailable, String sort,
                                              PageRequest of, HttpServletRequest request);

    EventFullDto getEventById(Long eventId, HttpServletRequest request);
}
