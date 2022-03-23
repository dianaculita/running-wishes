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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sport_id")
    private Long sportId;

    @Column(name = "sport_name")
    private String name;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "sport_id")
    private List<Competition> competitions;

}
