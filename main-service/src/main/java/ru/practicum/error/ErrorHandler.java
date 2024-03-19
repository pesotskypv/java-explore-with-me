package ru.practicum.error;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.error.model.ApiError;
import ru.practicum.exception.EntityConflictException;
import ru.practicum.exception.EntityNotFoundException;
import ru.practicum.exception.EntityValidationException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestControllerAdvice
public class ErrorHandler {
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleEntityNotFoundException(final EntityNotFoundException e) {
        return new ApiError(e.getMessage(), "Сущность не найдена или недоступна", HttpStatus.NOT_FOUND.name(),
                LocalDateTime.now().format(formatter));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleEntityConflictException(final EntityConflictException e) {
        return new ApiError(e.getMessage(), "Нарушение целостности данных", HttpStatus.CONFLICT.name(),
                LocalDateTime.now().format(formatter));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleEntityValidationException(final EntityValidationException e) {
        return new ApiError(e.getMessage(), "Валидация сущности не пройдена", HttpStatus.BAD_REQUEST.name(),
                LocalDateTime.now().format(formatter));
    }
}
