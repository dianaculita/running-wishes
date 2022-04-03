package com.project.models;

import lombok.*;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "PARTICIPANTS")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Participant {

    @Id
    @Column(name = "participant_cnp")
    private String cnp;

    @Column(name = "name")
    private String name;

    @ManyToMany(mappedBy = "participants")
    private List<Competition> competitions;

}
