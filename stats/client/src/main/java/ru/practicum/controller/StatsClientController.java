package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.model.EndpointHitDto;
import ru.practicum.model.EndpointHitDtoReq;
import ru.practicum.model.ViewStatsDto;
import ru.practicum.service.StatsClientService;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class StatsClientController {
    private final StatsClientService statsClientService;
    private static final String PATTERN = "yyyy-MM-dd HH:mm:ss";

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public EndpointHitDto addStat(@RequestBody EndpointHitDtoReq endpointHitDtoReq) {
        log.info("Получен POST-запрос /hit с телом={}", endpointHitDtoReq);

        return statsClientService.addStat(endpointHitDtoReq);
    }

    @GetMapping("/stats")
    public List<ViewStatsDto> findStats(@RequestParam @DateTimeFormat(pattern = PATTERN) LocalDateTime start,
                                        @RequestParam @DateTimeFormat(pattern = PATTERN) LocalDateTime end,
                                        @RequestParam(required = false) String[] uris,
                                        @RequestParam(defaultValue = "false") Boolean unique) {
        log.info("Получен GET-запрос /stats?start={}&end={}&uris={}&unique={}", start, end, uris, unique);

        return statsClientService.findStats(start, end, uris, unique);
    }
}
