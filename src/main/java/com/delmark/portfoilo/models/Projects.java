package com.delmark.portfoilo.models;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;

@Entity(name = "Projects")
@Table(name = "Projects")
@Accessors(chain = true)
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Setter
public class Projects {
    @Id
    @SequenceGenerator(name = "proj_id_seq", sequenceName = "proj_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "proj_id_seq")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "portfolio_id")
    private Portfolio portfolio;

    private String projectName;
    private String projectDesc;
    private String projectLink;

}
