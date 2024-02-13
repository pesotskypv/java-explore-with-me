package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.dao.StatsServerRepository;
import ru.practicum.exception.EntityValidationException;
import ru.practicum.mapper.EndpointHitMapper;
import ru.practicum.mapper.ViewStatsMapper;
import ru.practicum.model.EndpointHitDto;
import ru.practicum.model.EndpointHitDtoReq;
import ru.practicum.model.ViewStats;
import ru.practicum.model.ViewStatsDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class StatsServerServiceImpl implements StatsServerService {
    private final StatsServerRepository statsServerRepository;
    private final EndpointHitMapper endpointHitMapper;
    private final ViewStatsMapper viewStatsMapper;

    @Override
    public EndpointHitDto addStat(EndpointHitDtoReq endpointHitDtoReq) {
        return endpointHitMapper.toEndpointHitDto(statsServerRepository.save(endpointHitMapper
                .toEndpointHit(endpointHitDtoReq)));
    }

    @Override
    public List<ViewStatsDto> findStats(LocalDateTime start, LocalDateTime end, String[] uris, Boolean isUnique) {
        List<ViewStats> viewStats;

/*        if (end.isAfter(LocalDateTime.now()))
            throw new EntityValidationException("Дата конца периода не может быть позже текущей даты.");*/

        if (start.isAfter(end))
            throw new EntityValidationException("Дата начала периода не может быть позже даты конца периода.");

        if (isUnique) {
            if (uris != null) {
                viewStats = statsServerRepository.findStatsByUniqueIpAndUris(start, end, uris);
            } else {
                viewStats = statsServerRepository.findStatsByUniqueIp(start, end);
            }
        } else {
            if (uris != null) {
                viewStats = statsServerRepository.findStatsByUris(start, end, uris);
            } else {
                viewStats = statsServerRepository.findStats(start, end);
            }
        }

        return viewStats.stream().map(viewStatsMapper::toViewStatsDto).collect(Collectors.toList());
    }
}
