package com.delmark.portfoilo.models;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;

import java.util.Date;

@Table(name="Places_Of_Work")
@Entity(name="Places_Of_Work")
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Setter
@EqualsAndHashCode
public class PlacesOfWork {
    @Id
    @SequenceGenerator(name = "work_id_seq", sequenceName = "work_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "work_id_seq")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "Portfolio_id")
    private Portfolio portfolio;

    @Column(name = "workplace_name")
    private String workplaceName;

    @Column(name = "workplace_description")
    private String workplaceDesc;

    @Column(name = "Post")
    private String post;

    @Column(name = "date_of_hire")
    private Date hireDate;

    @Column(name = "date_of_fire")
    private Date fireDate;
}
