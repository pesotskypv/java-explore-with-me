package ru.practicum.compilation.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.service.CompilationService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping("/compilations")
@RequiredArgsConstructor
public class CompilationPublicController {
    private final CompilationService compilationService;

    @GetMapping
    public List<CompilationDto> findAllCompilation(@RequestParam(required = false) Boolean pinned,
                                                   @RequestParam(required = false, defaultValue = "0")
                                                   @PositiveOrZero int from,
                                                   @RequestParam(required = false, defaultValue = "10")
                                                       @Positive int size) {
        log.info("Получен GET-запрос /compilations?pinned={}&from={}&size={}", pinned, from, size);
        return compilationService.findAllCompilation(pinned, PageRequest.of(from / size, size));
    }

    @GetMapping("/{compId}")
    public CompilationDto getCompilationById(@PathVariable @PositiveOrZero Long compId) {
        log.info("Получен GET-запрос /compilations/{}", compId);
        return compilationService.getCompilationById(compId);
    }
}