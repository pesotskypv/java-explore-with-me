package ru.practicum.category.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewCategoryDto {
    @NotBlank
    @Size(min = 1, max = 50, message = "Максимальная длина 50 символов")
    private String name;
}
