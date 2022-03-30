package com.project.models;

import lombok.*;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "CHARITIES", uniqueConstraints = { @UniqueConstraint(columnNames = { "iban" }) })
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class CharityPerson {

    @Id
    @Column(name = "person_cnp")
    private String personCnp;

    @Column(name = "name")
    private String name;

    @Column(name = "age")
    private Integer age;

    @Column(name = "gender")
    private Character gender;

    @Column(name = "story")
    private String story;

    @Column(name = "iban")
    private Long iban;

    @Column(name = "needed_fund")
    private Double neededFund;

    @Column(name = "raised_fund")
    private Double raisedFund;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "association_id", nullable = false)
    private Association association;

    @OneToMany(fetch = FetchType.LAZY, cascade = { CascadeType.ALL })
    @JoinColumn(name = "person_cnp")
    private List<Donation> donations;

}
