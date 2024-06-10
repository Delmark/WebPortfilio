package com.delmark.portfoilo.models.DTO;

import com.delmark.portfoilo.models.portfoliodata.Portfolio;
import jakarta.validation.constraints.*;
import lombok.Value;
import org.hibernate.validator.constraints.URL;

import java.io.Serializable;

/**
 * DTO for {@link Portfolio}
 */
@Value
public class PortfolioDto implements Serializable {
    @Size(min = 2, max = 255, message = "Поле 'О себе' должно содержать от 2 до 255 символов")
    String aboutUser;

    @Size(min = 2, max = 64, message = "Поле 'Образование' должно содержать от 2 до 64 символов")
    String education;

    @Pattern(regexp =  "^\\+?\\d{1,3}?\\(\\d{3}\\)\\-?\\d{3}\\-?\\d{4}$", message = "Номер телефона должен быть корректным")
    String phone;

    @URL(message = "Некорректная ссылка на ваш сайт")
    String siteUrl;
}