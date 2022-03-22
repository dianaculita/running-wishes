package com.project.models;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserSportId implements Serializable {

    @Column(name = "user_cnp")
    private Long cnp;

    @Column(name = "sport_id")
    private Long sportId;
}
