package ru.practicum.category.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.category.model.CategoryEvents;
import ru.practicum.category.dao.CategoryRepository;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.NewCategoryDto;
import ru.practicum.category.mapper.CategoryMapper;
import ru.practicum.category.model.Category;
import ru.practicum.exception.EntityConflictException;
import ru.practicum.exception.EntityNotFoundException;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    public CategoryDto createCategory(NewCategoryDto newCategoryDto) {
        String name = newCategoryDto.getName();

        if (Objects.nonNull(categoryRepository.findByName(name))) {
            throw new EntityConflictException("Нарушение целостности данных");
        }

        CategoryDto categoryDto = categoryMapper.toCategoryDto(categoryRepository
                .save(categoryMapper.toCategory(newCategoryDto)));
        log.info("Добавлена категория: {}", categoryDto);

        return categoryDto;
    }

    @Override
    public void deleteCategory(Long catId) {
        CategoryEvents categoryEvents = categoryRepository.getCategoryNumbOfEvents(catId);

        if (categoryEvents == null) {
            throw new EntityNotFoundException("Категория не найдена или недоступна");
        } else if (categoryEvents.getNumbOfEvents() != 0) {
            throw new EntityConflictException("Существуют события, связанные с категорией");
        }

        categoryRepository.deleteById(catId);
        log.info("Удалена категория с id: {}", catId);
    }

    @Override
    public CategoryDto changeCategory(Long catId, NewCategoryDto newCategoryDto) {
        String name = newCategoryDto.getName();
        Category savedCategory = categoryRepository.findById(catId)
                .orElseThrow(() -> new EntityNotFoundException("Категория не найдена или недоступна"));
        Category foundCategory = categoryRepository.findByName(name);

        if (foundCategory != null && !foundCategory.getId().equals(catId)) {
            throw new EntityConflictException("Нарушение целостности данных");
        }

        savedCategory.setName(name);

        CategoryDto categoryDto = categoryMapper.toCategoryDto(categoryRepository.save(savedCategory));
        log.info("Изменена категория: {}", categoryDto);

        return categoryDto;
    }

    @Override
    public List<CategoryDto> findAllCategories(PageRequest of) {
        List<CategoryDto> categoryDtoList = categoryRepository.findAll(of).stream().map(categoryMapper::toCategoryDto)
                .collect(Collectors.toList());
        log.info("Возвращён список категорий: {}", categoryDtoList);

        return categoryDtoList;
    }

    @Override
    public CategoryDto getCategoryById(Long catId) {
        Category foundCategory = categoryRepository.findById(catId)
                .orElseThrow(() -> new EntityNotFoundException("Категория не найдена или недоступна"));
        log.info("Возвращена категория: {}", foundCategory);

        return categoryMapper.toCategoryDto(foundCategory);
    }
}
