package com.delmark.portfoilo.models;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;

@Table(name="Technologies")
@Entity(name="Technologies")
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Setter
@EqualsAndHashCode
public class Techs {
    @Id
    @SequenceGenerator(name = "tech_id_seq", sequenceName = "tech_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tech_id_seq")
    private Long id;

    @Column(name = "Technology_Name")
    private String techName;

    @Column(name = "Technology_Description")
    private String techDesc;
}
