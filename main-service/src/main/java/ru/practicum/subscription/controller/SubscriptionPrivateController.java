package ru.practicum.subscription.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.subscription.dto.SubscriptionDto;
import ru.practicum.subscription.service.SubscriptionService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping("/user/{userId}/subscriptions")
@RequiredArgsConstructor
public class SubscriptionPrivateController {
    private final SubscriptionService subscriptionService;

    @PostMapping("/{authorId}")
    @ResponseStatus(HttpStatus.CREATED)
    public SubscriptionDto createSubscription(@PathVariable(name = "userId") @PositiveOrZero Long subscriberId,
                                              @PathVariable @PositiveOrZero Long authorId) {
        log.info("Получен POST-запрос /user/{}/subscriptions/{}", subscriberId, authorId);
        return subscriptionService.createSubscription(subscriberId, authorId);
    }

    @DeleteMapping("/{authorId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSubscription(@PathVariable(name = "userId") @PositiveOrZero Long subscriberId,
                                       @PathVariable @PositiveOrZero Long authorId) {
        log.info("Получен DELETE-запрос /user/{}/subscriptions/{}", subscriberId, authorId);
        subscriptionService.deleteSubscription(subscriberId, authorId);
    }

    @GetMapping
    public List<SubscriptionDto> findSubscriptions(@PathVariable(name = "userId") @PositiveOrZero Long subscriberId,
                                                   @RequestParam(required = false, defaultValue = "0")
                                                       @PositiveOrZero int from,
                                                   @RequestParam(required = false, defaultValue = "10")
                                                       @Positive int size) {
        log.info("Получен GET-запрос /user/{}/subscriptions?from={}&size={}", subscriberId, from, size);
        return subscriptionService.findSubscriptions(subscriberId, PageRequest.of(from / size, size));
    }

    @GetMapping("/followers")
    public List<SubscriptionDto> findSubscribers(@PathVariable(name = "userId") @PositiveOrZero Long authorId,
                                                 @RequestParam(required = false, defaultValue = "0")
                                                     @PositiveOrZero int from,
                                                 @RequestParam(required = false, defaultValue = "10")
                                                     @Positive int size) {
        log.info("Получен GET-запрос /user/{}/subscriptions/followers?from={}&size={}", authorId, from, size);
        return subscriptionService.findSubscribers(authorId, PageRequest.of(from / size, size));
    }
}
