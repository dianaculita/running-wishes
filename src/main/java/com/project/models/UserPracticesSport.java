package com.project.models;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "USERS_PRACTICE_SPORTS")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class UserPracticesSport {

    @EmbeddedId
    private UserSportId userSportId;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("sportId")
    @JoinColumn(name = "sport_id", nullable = false)
    private Sport sport;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("cnp")
    @JoinColumn(name = "user_cnp", nullable = false)
    private User user;

    @Column(name = "burned_calories")
    private Long burnedCalories;

    @Column(name = "raised_sum")
    private Long raisedSum;

}
