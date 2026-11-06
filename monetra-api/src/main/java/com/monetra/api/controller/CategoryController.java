package com.monetra.api.controller;

import com.monetra.api.security.UserContext;
import com.monetra.core.dto.CategoryCreateDto;
import com.monetra.core.dto.CategoryDto;
import com.monetra.core.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
@Tag(name = "Categories", description = "Default and custom expense/income categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    @Operation(summary = "List all available categories (standard defaults + user custom)")
    public ResponseEntity<List<CategoryDto>> getCategories() {
        Long userId = UserContext.getRequiredUserId();
        return ResponseEntity.ok(categoryService.getCategoriesForUser(userId));
    }

    @PostMapping
    @Operation(summary = "Create a custom category")
    public ResponseEntity<CategoryDto> createCategory(@RequestBody CategoryCreateDto dto) {
        Long userId = UserContext.getRequiredUserId();
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryService.createCustomCategory(userId, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a user-created custom category")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        Long userId = UserContext.getRequiredUserId();
        categoryService.deleteCustomCategory(id, userId);
        return ResponseEntity.noContent().build();
    }
}

