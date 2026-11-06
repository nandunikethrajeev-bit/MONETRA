package com.monetra.core.dto;

import com.monetra.core.model.TransactionType;

public class CategoryCreateDto {
    private String name;
    private TransactionType type;
    private String icon;
    private String color;

    public CategoryCreateDto() {
        this.type = TransactionType.EXPENSE;
        this.icon = "tag";
        this.color = "#E5A93C";
    }

    public CategoryCreateDto(String name, TransactionType type, String icon, String color) {
        this.name = name;
        this.type = type != null ? type : TransactionType.EXPENSE;
        this.icon = icon != null ? icon : "tag";
        this.color = color != null ? color : "#E5A93C";
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
}
