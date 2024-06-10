package com.delmark.portfoilo.models.DTO;

import com.delmark.portfoilo.models.portfoliodata.Projects;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Value;
import org.hibernate.validator.constraints.URL;

import java.io.Serializable;

/**
 * DTO for {@link Projects}
 */
@Value
public class ProjectsDto implements Serializable {
    @Size(min = 2, max = 32, message = "Размер названия должен быть от 2 до 32 символов")
    @NotNull(message = "У проекта должно быть имя")
    String projectName;

    @Size(min = 2, max = 128, message = "Размер описания должен быть от 2 до 128 символов")
    @NotNull(message = "У проекта должно быть описание")
    String projectDesc;

    @URL(message = "Некорректная ссылка на проект")
    String projectLink;
}