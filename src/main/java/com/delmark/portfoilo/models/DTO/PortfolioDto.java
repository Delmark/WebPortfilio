package com.delmark.portfoilo.models.DTO;

import jakarta.validation.constraints.*;
import lombok.Value;
import org.hibernate.validator.constraints.URL;

import java.io.Serializable;

/**
 * DTO for {@link com.delmark.portfoilo.models.Portfolio}
 */
@Value
public class PortfolioDto implements Serializable {
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

    @Size(min = 2, max = 255, message = "Поле 'О себе' должно содержать от 2 до 255 символов")
    String aboutUser;

    @Size(min = 2, max = 64, message = "Поле 'Образование' должно содержать от 2 до 64 символов")
    String education;

    @Size(min = 2, max = 64, message = "Поле 'Почта' должен составять от 2 до 64 символов")
    @Email(message = "Некорректная почта")
    String email;

    @Pattern(regexp =  "^\\+\\d{1,3}\\(\\d{3}\\)\\d{3}-\\d{4}$", message = "Номер телефона должен быть корректным")
    String phone;

    @URL(message = "Некорректная ссылка на ваш сайт")
    String siteUrl;
}