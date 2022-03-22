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
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "donation_id")
    private Long donationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_cnp", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "person_cnp", nullable = false)
    private CharityPerson charityPerson;

    @Column(name = "total_funds")
    private Long totalFunds;

}
