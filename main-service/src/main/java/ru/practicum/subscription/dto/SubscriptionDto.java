package ru.practicum.subscription.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.user.dto.UserShortDto;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubscriptionDto {
    private Long id;
    private UserShortDto author;
    private UserShortDto subscriber;
    private String created;
}
