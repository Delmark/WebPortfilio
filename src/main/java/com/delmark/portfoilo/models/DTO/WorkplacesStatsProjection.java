package com.delmark.portfoilo.models.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

public interface WorkplacesStatsProjection {
    String getWorkplaceName();
    Integer getCount();
}
