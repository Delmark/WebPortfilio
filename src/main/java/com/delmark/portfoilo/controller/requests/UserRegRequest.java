package com.delmark.portfoilo.controller.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Value;

@Value
public class UserRegRequest {
    @NotBlank(message = "Логин не должен быть пустым")
    @Size(min = 5, max = 32, message = "Логин должен содержать в себе от 5 до 32 символов")
    @NotNull(message = "Требуется логин")
    String username;

    @NotNull(message = "Требуется пароль")
    @NotBlank(message = "Пароль не должен быть пустым")
    @Size(min = 5, max = 32, message = "Пароль должен быть не меньше от 5 до 32 символов")
    String password;

    @NotNull
    @NotBlank(message = "Поле имени")
    @Size(min = 2, max = 32, message = "Поле 'Имя' должно содержать ото 2 до 32 символов")
    String name;

    @NotBlank
    @Size(min = 2, max = 32, message = "Поле 'Фамилия' должно содержать от 2 до 32 символов")
    @NotNull
    String surname;

    @Size(min = 2, max = 32, message = "Поле 'Отчество' должно содержать от 2 до 32")
    String middleName;

    @Size(min = 2, max = 64, message = "Поле 'Почта' должен составять от 2 до 64 символов")
    @Email(message = "Некорректная почта")
    String email;
}
