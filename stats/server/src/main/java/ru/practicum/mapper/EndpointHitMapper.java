package ru.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.model.EndpointHit;
import ru.practicum.model.EndpointHitDto;
import ru.practicum.model.EndpointHitDtoReq;

@Mapper(componentModel = org.mapstruct.MappingConstants.ComponentModel.SPRING)
public interface EndpointHitMapper {
    @Mapping(target = "timestamp", dateFormat = "yyyy-MM-dd HH:mm:ss")
    EndpointHit toEndpointHit(EndpointHitDtoReq endpointHitDtoReq);

    @Mapping(target = "timestamp", dateFormat = "yyyy-MM-dd HH:mm:ss")
    EndpointHitDto toEndpointHitDto(EndpointHit endpointHit);
}
