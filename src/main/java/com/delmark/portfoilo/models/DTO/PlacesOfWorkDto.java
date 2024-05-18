package com.delmark.portfoilo.models.DTO;

import com.delmark.portfoilo.models.Workplace;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Value;

import java.io.Serializable;
import java.util.Date;

/**
 * DTO for {@link Workplace}
 */
@Value
public class PlacesOfWorkDto implements Serializable {
    @NotNull
    String workplaceName;
    @NotNull
    String workplaceDesc;
    String post;
    @NotNull
    @PastOrPresent
    Date hireDate;
    Date fireDate;
}