package ru.practicum.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.UpdateEventAdminRequest;
import ru.practicum.event.service.EventService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping("/admin/events")
@RequiredArgsConstructor
public class EventAdminController {
    private final EventService eventService;

    @GetMapping
    public List<EventFullDto> findAllEvents(@RequestParam(required = false) Long[] users,
                                            @RequestParam(required = false) String[] states,
                                            @RequestParam(required = false) Long[] categories,
                                            @RequestParam(required = false)
                                                @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                                                LocalDateTime rangeStart,
                                            @RequestParam(required = false)
                                                @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
                                            @RequestParam(required = false, defaultValue = "0")
                                                @PositiveOrZero int from,
                                            @RequestParam(required = false, defaultValue = "10") @Positive int size) {
        log.info("Получен GET-запрос " +
                        "/admin/events?users={}&states={}&categories={}&rangeStart={}&rangeEnd={}&from={}&size={}",
                users, states, categories, rangeStart, rangeEnd, from, size);
        return eventService.findAllEvents(users, states, categories, rangeStart, rangeEnd,
                PageRequest.of(from / size, size));
    }

    @PatchMapping("/{eventId}")
    public EventFullDto changeEventByAdmin(@PathVariable @PositiveOrZero Long eventId,
                                           @RequestBody @Valid UpdateEventAdminRequest request) {
        log.info("Получен PATCH-запрос /admin/events/{} с телом={}", eventId, request);
        return eventService.changeEventByAdmin(eventId, request);
    }
}
