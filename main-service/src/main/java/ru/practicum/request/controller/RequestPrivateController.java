package ru.practicum.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.service.RequestService;

import javax.validation.constraints.PositiveOrZero;
import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;

@Slf4j
@Validated
@RestController
@RequestMapping("/users/{userId}/requests")
@RequiredArgsConstructor
public class RequestPrivateController {
    private final RequestService requestService;

    @GetMapping
    public List<ParticipationRequestDto> getRequests(@PathVariable @PositiveOrZero Long userId) {
        return requestService.getRequests(userId);
    }

    @PostMapping
    @ResponseStatus(CREATED)
    public ParticipationRequestDto createRequest(@PathVariable @PositiveOrZero Long userId,
                                                 @RequestParam @PositiveOrZero Long eventId) {
        return requestService.createRequest(userId, eventId);
    }

    @PatchMapping("/{requestId}/cancel")
    public ParticipationRequestDto cancelRequest(@PathVariable @PositiveOrZero Long userId,
                                                 @PathVariable @PositiveOrZero Long requestId) {
        return requestService.cancelRequest(userId, requestId);
    }
}
