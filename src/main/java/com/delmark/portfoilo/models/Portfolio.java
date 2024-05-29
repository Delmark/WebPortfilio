package com.delmark.portfoilo.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.validator.constraints.URL;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity(name = "portfolio")
@Table(name = "portfolio")
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@EqualsAndHashCode
@Setter
@ToString
public class Portfolio {
    @Id
    @SequenceGenerator(name = "port_id_seq", sequenceName = "port_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "port_id_seq")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "name")
    private String name;

    @Column(name = "surname")
    private String surname;

    @Column(name = "middle_name")
    @Nullable
    private String middleName;

    @Column(name = "about")
    private String aboutUser;

    @Column(name = "education")
    private String education;

    @Column(name = "email")
    private String email;

    @Column(name = "phone")
    @Nullable
    private String phone;

    @Column(name = "site")
    @Nullable
    private String siteUrl;

    @ToString.Exclude
    @JsonIgnore
    @ManyToMany(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    @JoinTable(name = "portfolio_techses",
            joinColumns = @JoinColumn(name = "portfolio_id"),
            inverseJoinColumns = @JoinColumn(name = "techses_id"))
    private Set<Techs> techses = new LinkedHashSet<>();

    @ToString.Exclude
    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    Set<Projects> projects = new HashSet<>();

    @ToString.Exclude
    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    Set<Workplace> workplaces = new HashSet<>();
}
