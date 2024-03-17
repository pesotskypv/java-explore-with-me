package ru.practicum.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.service.EventService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class EventPublicController {
    private final EventService eventService;
    private static final String PATTERN = "yyyy-MM-dd HH:mm:ss";

    @GetMapping
    public List<EventShortDto> findAllEventsByParams(@RequestParam(required = false, name = "text") String text,
                                                     @RequestParam(required = false, name = "categories")
                                                     Long[] categories,
                                                     @RequestParam(required = false, name = "paid") Boolean paid,
                                                     @RequestParam(required = false, name = "rangeStart")
                                                     @DateTimeFormat(pattern = PATTERN) LocalDateTime rangeStart,
                                                     @RequestParam(required = false, name = "rangeEnd")
                                                     @DateTimeFormat(pattern = PATTERN) LocalDateTime rangeEnd,
                                                     @RequestParam(required = false, name = "onlyAvailable",
                                                             defaultValue = "false") Boolean onlyAvailable,
                                                     @RequestParam(required = false, name = "sort") String sort,
                                                     @RequestParam(required = false, name = "from", defaultValue = "0")
                                                         @PositiveOrZero int from,
                                                     @RequestParam(required = false, name = "size", defaultValue = "10")
                                                         @Positive int size,
                                                     HttpServletRequest request) {
        log.info("Получен GET-запрос /events?text={}&categories={}&paid={}&rangeStart={}&rangeEnd={}&onlyAvailable={}" +
                "&sort={}&from={}&size={}&request={}", text, categories, paid, rangeStart, rangeEnd, onlyAvailable,
                sort, from, size, request);
        return eventService.findAllEventsByParams(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort,
                PageRequest.of(from / size, size), request);
    }

    @GetMapping("/{id}")
    public EventFullDto getEventById(@PathVariable(name = "id") @PositiveOrZero Long eventId,
                                     HttpServletRequest request) {
        log.info("Получен GET-запрос /events/{}", eventId);
        return eventService.getEventById(eventId, request);
    }
}
