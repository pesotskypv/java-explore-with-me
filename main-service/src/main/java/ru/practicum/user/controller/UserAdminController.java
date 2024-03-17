package ru.practicum.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.user.dto.NewUserRequest;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.service.UserService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor
public class UserAdminController {
    private final UserService userService;

    @GetMapping
    public List<UserDto> findAllUsers(@RequestParam(name = "ids", required = false) Long[] ids,
                                      @RequestParam(required = false, name = "from", defaultValue = "0")
                                      @PositiveOrZero int from,
                                      @RequestParam(required = false, name = "size", defaultValue = "10")
                                      @Positive int size) {
        log.info("Получен GET-запрос /admin/users?ids={}&from={}&size={}", ids, from, size);
        return userService.findAllUsers(ids, PageRequest.of(from / size, size));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto createUser(@RequestBody @Valid NewUserRequest newUserRequest) {
        log.info("Получен POST-запрос /admin/users с телом={}", newUserRequest);
        return userService.createUser(newUserRequest);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable(name = "userId") @PositiveOrZero Long userId) {
        log.info("Получен DELETE-запрос /admin/users/{}", userId);
        userService.deleteUser(userId);
    }
}
