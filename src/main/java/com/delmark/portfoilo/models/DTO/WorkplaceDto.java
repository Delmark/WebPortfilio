package com.delmark.portfoilo.models.DTO;

import com.delmark.portfoilo.models.portfoliodata.Workplace;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.Value;

import java.io.Serializable;
import java.util.Date;

/**
 * DTO for {@link Workplace}
 */
@Value
public class WorkplaceDto implements Serializable {
    @NotNull
    @NotBlank
    @Size(min = 3, max = 255)
    String workplaceName;

    @NotNull
    @NotBlank
    @Size(min = 3, max = 255)
    String workplaceDesc;

    @NotBlank
    @Size(min = 3, max = 120)
    String post;

    @NotNull
    @PastOrPresent
    Date hireDate;

    @PastOrPresent
    Date fireDate;
}