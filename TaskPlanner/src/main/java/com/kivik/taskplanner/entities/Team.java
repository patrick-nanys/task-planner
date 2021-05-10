package com.kivik.taskplanner.entities;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
public class Team {
    @Id
    @GeneratedValue
    private Long id;
    @NotBlank
    private String name;

    @OneToOne
    private User owner;

    @ToString.Exclude
    @ManyToMany(fetch = FetchType.LAZY)
    private List<User> members;

    public Team(String name) {
        this.name = name;
    }
}
