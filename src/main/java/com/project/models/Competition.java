package com.project.models;

import lombok.*;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "COMPETITIONS", uniqueConstraints = { @UniqueConstraint(columnNames = { "name" }) })
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Competition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "competition_id")
    private Long competitionId;

    @Column(name = "name")
    private String name;

    @Column(name = "number_of_days")
    private Integer numberOfDays;

    @Column(name = "location")
    private String location;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sport_id", nullable = false)
    private Sport sport;

    @ManyToMany(mappedBy = "participatesToCompetitions")
    private List<User> users;

    @ManyToMany
    @JoinTable(
            name = "sponsor_has",
            joinColumns = @JoinColumn(name = "competition_id"),
            inverseJoinColumns = @JoinColumn(name = "sponsor_id")
    )
    private List<Sponsor> sponsors;

}
