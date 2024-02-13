package ru.practicum.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.client.BaseClient;
import ru.practicum.model.EndpointHitDto;
import ru.practicum.model.EndpointHitDtoReq;
import ru.practicum.model.ViewStatsDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class StatsClientService extends BaseClient {

    @Autowired
    public StatsClientService(@Value("${stats-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public EndpointHitDto addStat(EndpointHitDtoReq endpointHitDtoReq) {
        return (EndpointHitDto) post("/hit", endpointHitDtoReq, new EndpointHitDto()).getBody();
    }

    public List<ViewStatsDto> findStats(LocalDateTime start, LocalDateTime end, String[] uris, Boolean unique) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        Map<String, Object> parameters = Map.of(
                "start", start.format(formatter),
                "end", end.format(formatter),
                "uris", uris,
                "unique", unique.toString()
        );

        ResponseEntity<ArrayList<ViewStatsDto>> responseEntity =
                get("/stats?start={start}&end={end}&uris={uris}&uniq={unique}", parameters, new ArrayList<>());

        return responseEntity.getBody();
    }
}
