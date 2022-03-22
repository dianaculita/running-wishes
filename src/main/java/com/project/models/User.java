package com.project.models;

import lombok.*;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "USERS", uniqueConstraints = { @UniqueConstraint(columnNames = { "sport_name" }) })
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class User {

    @Id
    @Column(name = "user_cnp")
    private Long cnp;

    @Column(name = "name")
    private String name;

    @Column(name = "age")
    private Integer age;

    @Column(name = "gender")
    private Character gender;

    @OneToMany
    @JoinColumn(name = "user_cnp")
    private List<UserPracticesSport> userPracticesSports;

    @OneToMany
    @JoinColumn(name = "user_cnp")
    private List<Donation> donations;

}
