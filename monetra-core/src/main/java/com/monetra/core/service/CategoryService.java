package com.monetra.core.service;

import com.monetra.core.dto.CategoryCreateDto;
import com.monetra.core.dto.CategoryDto;
import com.monetra.core.exception.ResourceNotFoundException;
import com.monetra.core.exception.ValidationException;
import com.monetra.core.model.Category;
import com.monetra.core.repository.CategoryRepository;
import com.monetra.core.validation.FinancialValidator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<CategoryDto> getCategoriesForUser(Long userId) {
        return categoryRepository.findByUserId(userId)
                .stream()
                .map(CategoryDto::fromEntity)
                .collect(Collectors.toList());
    }

    public CategoryDto getCategoryById(Long categoryId, Long userId) {
        Category category = categoryRepository.findByIdAndUserId(categoryId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", categoryId));
        return CategoryDto.fromEntity(category);
    }

    @Transactional
    public CategoryDto createCustomCategory(Long userId, CategoryCreateDto dto) {
        FinancialValidator.validateNotBlank(dto.getName(), "Category Name");

        Category category = new Category();
        category.setUserId(userId);
        category.setName(dto.getName().trim());
        category.setType(dto.getType());
        category.setIcon(dto.getIcon() != null ? dto.getIcon() : "tag");
        category.setColor(dto.getColor() != null ? dto.getColor() : "#E5A93C");
        category.setDefault(false);

        Category saved = categoryRepository.save(category);
        return CategoryDto.fromEntity(saved);
    }

    @Transactional
    public void deleteCustomCategory(Long categoryId, Long userId) {
        Category category = categoryRepository.findByIdAndUserId(categoryId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", categoryId));

        if (category.isDefault()) {
            throw new ValidationException("System default categories cannot be deleted");
        }

        categoryRepository.delete(categoryId, userId);
    }
}
