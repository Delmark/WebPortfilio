package com.delmark.portfoilo.models.DTO;

import jakarta.validation.constraints.NotNull;
import lombok.Value;

import java.io.Serializable;

/**
 * DTO for {@link com.delmark.portfoilo.models.Projects}
 */
@Value
public class ProjectsDto implements Serializable {
    @NotNull
    String projectName;
    @NotNull
    String projectDesc;
    String projectLink;
}