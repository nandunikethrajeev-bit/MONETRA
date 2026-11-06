package com.monetra.core.repository;

import com.monetra.core.model.Category;
import java.util.List;
import java.util.Optional;

public interface CategoryRepository {
    Optional<Category> findById(Long id);
    Optional<Category> findByIdAndUserId(Long id, Long userId);
    List<Category> findByUserId(Long userId);
    List<Category> findDefaultCategories();
    Category save(Category category);
    void update(Category category);
    void delete(Long id, Long userId);
}

