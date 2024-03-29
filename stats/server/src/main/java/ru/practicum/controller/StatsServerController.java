package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.model.EndpointHitDto;
import ru.practicum.model.EndpointHitDtoReq;
import ru.practicum.model.ViewStatsDto;
import ru.practicum.service.StatsServerService;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class StatsServerController {
    private final StatsServerService statsServerService;
    private static final String PATTERN = "yyyy-MM-dd HH:mm:ss";

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public EndpointHitDto addStat(@RequestBody EndpointHitDtoReq endpointHitDtoReq) {
        log.info("Получен POST-запрос /hit с телом={}", endpointHitDtoReq);

        return statsServerService.addStat(endpointHitDtoReq);
    }

    @GetMapping("/stats")
    public List<ViewStatsDto> findStats(@RequestParam @DateTimeFormat(pattern = PATTERN) LocalDateTime start,
                                        @RequestParam @DateTimeFormat(pattern = PATTERN) LocalDateTime end,
                                        @RequestParam(required = false) String[] uris,
                                        @RequestParam(defaultValue = "false") Boolean unique) {
        log.info("Получен GET-запрос /stats?start={}&end={}&uris={}&unique={}", start, end, uris, unique);

        return statsServerService.findStats(start, end, uris, unique);
    }
}
