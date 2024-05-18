package com.delmark.portfoilo.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.Date;

@Table(name="workplace")
@Entity(name="workplace")
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Setter
@EqualsAndHashCode
public class Workplace {
    @Id
    @SequenceGenerator(name = "work_id_seq", sequenceName = "work_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "work_id_seq")
    private Long id;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "portfolio_id")
    private Portfolio portfolio;

    @Column(name = "workplace_name")
    private String workplaceName;

    @Column(name = "workplace_description")
    private String workplaceDesc;

    @Column(name = "Post")
    private String post;

    @JsonFormat(pattern = "dd-MM-yyyy")
    @Column(name = "date_of_hire")
    private Date hireDate;

    @JsonFormat(pattern = "dd-MM-yyyy")
    @Column(name = "date_of_fire")
    private Date fireDate;
}
