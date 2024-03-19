package ru.practicum.category.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.category.model.Category;
import ru.practicum.category.model.CategoryEvents;


public interface CategoryRepository extends JpaRepository<Category, Long> {
    Category findByName(String name);

    @Query(value = "SELECT c.id AS catId, COUNT(e.id) AS numbOfEvents " +
            "FROM categories c " +
            "LEFT JOIN events e ON c.id = e.category_id " +
            "WHERE c.id = :catId " +
            "GROUP BY c.id",
            nativeQuery = true)
    CategoryEvents getCategoryNumbOfEvents(Long catId);
}
