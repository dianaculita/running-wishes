package com.project.models;

import lombok.*;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "SPONSORS", uniqueConstraints = { @UniqueConstraint(columnNames = { "name" }) })
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Sponsor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sponsor_id")
    private Long sponsorId;

    @Column(name = "name")
    private String name;

    @Column(name = "sponsoring_funds")
    private Double sponsoringFunds;

    @ManyToMany(mappedBy = "sponsors")
    private List<Competition> competitions;

}
