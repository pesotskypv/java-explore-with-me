package ru.practicum.subscription.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import ru.practicum.subscription.dto.SubscriptionDto;
import ru.practicum.subscription.model.Subscription;
import ru.practicum.user.mapper.UserMapper;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = {UserMapper.class})
public interface SubscriptionMapper {
    @Mapping(target = "created", dateFormat = "yyyy-MM-dd HH:mm:ss")
    SubscriptionDto toSubscriptionDto(Subscription subscription);
}
