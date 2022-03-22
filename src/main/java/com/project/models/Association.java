package com.project.models;

import lombok.*;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "ASSOCIATIONS")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Association {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "association_id")
    private Long associationId;

    @Column(name = "name")
    private String name;

    @Column(name = "purpose")
    private String purpose;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "association_id")
    private List<CharityPerson> charityPeople;

}
