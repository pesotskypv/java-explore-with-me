package ru.practicum.category.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.NewCategoryDto;
import ru.practicum.category.service.CategoryService;

import javax.validation.Valid;
import javax.validation.constraints.PositiveOrZero;

@Slf4j
@Validated
@RestController
@RequestMapping("/admin/categories")
@RequiredArgsConstructor
public class CategoryAdminController {
    private final CategoryService categoryService;

    @PostMapping
    @ResponseStatus(value = HttpStatus.CREATED)
    public CategoryDto createCategory(@RequestBody @Valid NewCategoryDto newCategoryDto) {
        log.info("Получен POST-запрос /admin/categories с телом={}", newCategoryDto);
        return categoryService.createCategory(newCategoryDto);
    }

    @DeleteMapping("/{catId}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable(name = "catId") @PositiveOrZero Long catId) {
        log.info("Получен DELETE-запрос /admin/categories/{}", catId);
        categoryService.deleteCategory(catId);
    }

    @PatchMapping("/{catId}")
    public CategoryDto changeCategory(@PathVariable(name = "catId") @PositiveOrZero Long catId,
                                      @RequestBody @Valid NewCategoryDto newCategoryDto) {
        log.info("Получен PATCH-запрос /admin/categories/{} с телом={}", catId, newCategoryDto);
        return categoryService.changeCategory(catId, newCategoryDto);
    }
}
