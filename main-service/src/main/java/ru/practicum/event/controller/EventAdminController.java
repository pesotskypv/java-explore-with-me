package ru.practicum.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
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
    public List<EventFullDto> findAllEvents(@RequestParam(required = false, name = "users") Long[] users,
                                            @RequestParam(required = false, name = "states") String[] states,
                                            @RequestParam(required = false, name = "categories") Long[] categories,
                                            @RequestParam(required = false, name = "rangeStart")
                                                @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                                                LocalDateTime rangeStart,
                                            @RequestParam(required = false, name = "rangeEnd")
                                                @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
                                            @RequestParam(required = false, name = "from", defaultValue = "0")
                                            @PositiveOrZero int from,
                                            @RequestParam(required = false, name = "size", defaultValue = "10")
                                            @Positive int size) {
        log.info("Получен GET-запрос " +
                        "/admin/events?users={}&states={}&categories={}&rangeStart={}&rangeEnd={}&from={}&size={}",
                users, states, categories, rangeStart, rangeEnd, from, size);
        return eventService.findAllEvents(users, states, categories, rangeStart, rangeEnd,
                PageRequest.of(from / size, size));
    }

    @PatchMapping("/{eventId}")
    public EventFullDto changeEventByAdmin(@PathVariable(name = "eventId") @PositiveOrZero Long eventId,
                                           @RequestBody @Valid UpdateEventAdminRequest request) {
        log.info("Получен PATCH-запрос /admin/events/{} с телом={}", eventId, request);
        return eventService.changeEventByAdmin(eventId, request);
    }
}
