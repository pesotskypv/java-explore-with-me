package ru.practicum.category.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.service.CategoryService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryPublicController {
    private final CategoryService categoryService;

    @GetMapping
    public List<CategoryDto> findAllCategories(@RequestParam(required = false, name = "from", defaultValue = "0")
                                              @PositiveOrZero int from,
                                              @RequestParam(required = false, name = "size", defaultValue = "10")
                                              @Positive int size) {
        log.info("Получен GET-запрос /categories?from={}&size={}", from, size);
        return categoryService.findAllCategories(PageRequest.of(from / size, size));
    }

    @GetMapping("/{catId}")
    public CategoryDto getCategoryById(@PathVariable(name = "catId") @PositiveOrZero Long catId) {
        log.info("Получен GET-запрос /categories/{}", catId);
        return categoryService.getCategoryById(catId);
    }
}
