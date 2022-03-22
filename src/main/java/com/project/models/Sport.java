package com.project.models;

import lombok.*;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "SPORTS", uniqueConstraints = { @UniqueConstraint(columnNames = { "sport_name" }) })
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Sport {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "sport_id")
    private Long sportId;

    @Column(name = "sport_name")
    private String name;

    @Column(name = "donation_amount") // each sport has a price (€) for donation per 500 burned calories
    private Long donation;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "sport_id")
    private List<UserPracticesSport> sportPracticedByUsers;

}
