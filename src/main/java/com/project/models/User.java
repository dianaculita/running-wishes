package com.project.models;

import lombok.*;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "USERS")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class User {

    @Id
    @Column(name = "user_cnp")
    private String cnp;

    @Column(name = "name")
    private String name;

    @Column(name = "age")
    private Integer age;

    @Column(name = "gender")
    private Character gender;

    @ManyToMany
    @JoinTable(
            name = "participates_to",
            joinColumns = @JoinColumn(name = "user_cnp"),
            inverseJoinColumns = @JoinColumn(name = "competition_id")
    )
    private List<Competition> participatesToCompetitions;

}