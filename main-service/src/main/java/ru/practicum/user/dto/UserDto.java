package ru.practicum.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private Long id;
    private String email;
    private String name;
    private Boolean subscriptionBanned;
}
