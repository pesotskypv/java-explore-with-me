package ru.practicum.mapper;

import org.mapstruct.Mapper;
import ru.practicum.model.ViewStats;
import ru.practicum.model.ViewStatsDto;

@Mapper(componentModel = org.mapstruct.MappingConstants.ComponentModel.SPRING,
        uses = {ViewStats.class, ViewStatsDto.class})
public interface ViewStatsMapper {
    ViewStatsDto toViewStatsDto(ViewStats viewStats);
}
