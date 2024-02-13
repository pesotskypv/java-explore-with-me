package ru.practicum.service;

import ru.practicum.model.EndpointHitDto;
import ru.practicum.model.EndpointHitDtoReq;
import ru.practicum.model.ViewStatsDto;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsServerService {
    EndpointHitDto addStat(EndpointHitDtoReq endpointHitDtoReq);

    List<ViewStatsDto> findStats(LocalDateTime start, LocalDateTime end, String[] uris, Boolean unique);
}
