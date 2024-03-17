package ru.practicum.category.service;

import org.springframework.data.domain.PageRequest;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.NewCategoryDto;

import java.util.List;

public interface CategoryService {
    CategoryDto createCategory(NewCategoryDto newCategoryDto);

    void deleteCategory(Long catId);

    CategoryDto changeCategory(Long catId, NewCategoryDto newCategoryDto);

    List<CategoryDto> findAllCategories(PageRequest of);

    CategoryDto getCategoryById(Long catId);
}
