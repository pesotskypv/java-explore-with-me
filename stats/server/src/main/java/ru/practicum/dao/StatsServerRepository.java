package ru.practicum.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.model.EndpointHit;
import ru.practicum.model.ViewStats;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsServerRepository extends JpaRepository<EndpointHit, Long> {
    @Query("SELECT distinct new ru.practicum.model.ViewStats (e.app, e.uri, COUNT(DISTINCT e.ip) AS hits) " +
            "FROM EndpointHit e " +
            "WHERE e.uri IN (:uris) AND e.timestamp BETWEEN :start AND :end " +
            "GROUP BY e.ip, e.uri, e.app " +
            "ORDER BY hits DESC")
    List<ViewStats> findStatsByUniqueIpAndUris(LocalDateTime start, LocalDateTime end, String[] uris);

    @Query("SELECT distinct new ru.practicum.model.ViewStats (e.app, e.uri, COUNT(DISTINCT e.ip) AS hits) " +
            "FROM EndpointHit e " +
            "WHERE e.timestamp BETWEEN :start AND :end " +
            "GROUP BY e.ip, e.uri, e.app " +
            "ORDER BY hits DESC")
    List<ViewStats> findStatsByUniqueIp(LocalDateTime start, LocalDateTime end);

    @Query("SELECT new ru.practicum.model.ViewStats (e.app, e.uri, COUNT(e.ip) AS hits) " +
            "FROM EndpointHit e " +
            "WHERE e.uri IN (:uris) AND e.timestamp BETWEEN :start AND :end " +
            "GROUP BY e.ip, e.uri, e.app " +
            "ORDER BY hits DESC")
    List<ViewStats> findStatsByUris(LocalDateTime start, LocalDateTime end, String[] uris);

    @Query("SELECT new ru.practicum.model.ViewStats (e.app, e.uri, COUNT(e.ip) AS hits) " +
            "FROM EndpointHit e " +
            "WHERE e.timestamp BETWEEN :start AND :end " +
            "GROUP BY e.ip, e.uri, e.app " +
            "ORDER BY hits DESC")
    List<ViewStats> findStats(LocalDateTime start, LocalDateTime end);
}
