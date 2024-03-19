package ru.practicum.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewUserRequest {
    @Email
    @NotBlank
    @Length(min = 6, max = 254, message = "Максимальная длина 254 символа")
    private String email;

    @NotBlank
    @Length(min = 2, max = 250, message = "Максимальная длина 250 символов")
    private String name;
}
