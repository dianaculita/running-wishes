package com.project.models;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "DONATIONS")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Donation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "donation_id")
    private Long donationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "person_cnp", nullable = false)
    private CharityPerson charityPerson;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sponsor_id", nullable = false)
    private Sponsor sponsor;

    @Column(name = "total_funds")
    private Long totalFunds;

}
