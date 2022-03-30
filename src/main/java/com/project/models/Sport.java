package com.project.models;

import lombok.*;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "SPORTS", uniqueConstraints = { @UniqueConstraint(columnNames = { "name" }) })
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

    @Column(name = "name")
    private String name;

    @OneToMany(fetch = FetchType.LAZY, cascade = { CascadeType.ALL })
    @JoinColumn(name = "sport_id")
    private List<Competition> competitions;

}
