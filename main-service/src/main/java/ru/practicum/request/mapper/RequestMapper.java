package ru.practicum.request.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.model.ParticipationRequest;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface RequestMapper {
    @Mapping(target = "requester", source = "participationRequest.requester.id")
    @Mapping(target = "event", source = "participationRequest.event.id")
    @Mapping(target = "created", dateFormat = "yyyy-MM-dd HH:mm:ss")
    ParticipationRequestDto toParticipationRequestDto(ParticipationRequest participationRequest);
}
