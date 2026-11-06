package com.monetra.core.dto;

import com.monetra.core.model.Category;
import com.monetra.core.model.TransactionType;

public class CategoryDto {
    private Long id;
    private Long userId;
    private String name;
    private TransactionType type;
    private String icon;
    private String color;
    private boolean isDefault;

    public CategoryDto() {
    }

    public CategoryDto(Long id, Long userId, String name, TransactionType type, String icon, String color, boolean isDefault) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.type = type;
        this.icon = icon;
        this.color = color;
        this.isDefault = isDefault;
    }

    public static CategoryDto fromEntity(Category category) {
        if (category == null) return null;
        return new CategoryDto(
                category.getId(),
                category.getUserId(),
                category.getName(),
                category.getType(),
                category.getIcon(),
                category.getColor(),
                category.isDefault()
        );
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean aDefault) {
        isDefault = aDefault;
    }
}

