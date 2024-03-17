package ru.practicum.exception;

public class EntityConflictException extends RuntimeException {
    public EntityConflictException(String message) {
        super(message);
    }
}
