package com.example.campusstore.dto;

import jakarta.validation.constraints.NotBlank;

public class CategoryForm {

    @NotBlank
    private String name;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}