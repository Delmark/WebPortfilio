package com.delmark.portfoilo.models.DTO;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Value;


@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WorkplacesStatsDTO {
    @Id
    String workplaceName;
    Long count;
}
