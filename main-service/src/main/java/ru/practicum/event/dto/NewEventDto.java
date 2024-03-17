package ru.practicum.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewEventDto {
    @NotBlank
    @Size(min = 20, max = 2000)
    private String annotation;

    @PositiveOrZero
    private Long category;

    @NotBlank
    @Size(min = 20, max = 7000)
    private String description;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @NotNull
    private LocalDateTime eventDate;

    private Location location;
    private Boolean paid;

    @PositiveOrZero
    private Integer participantLimit;
    private Boolean requestModeration;

    @NotBlank
    @Size(min = 3, max = 120)
    private String title;
}
