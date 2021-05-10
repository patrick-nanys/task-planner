package com.kivik.taskplanner.entities;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import java.util.Collection;

@Entity
@Data
@NoArgsConstructor
public class Privilege {

    public static final String USER_ADMIN = "USER_ADMIN";
    public static final String TASK_ADMIN = "TASK_ADMIN";
    public static final String TEAM_ADMIN = "TEAM_ADMIN";
    public static final String ROLE_ADMIN = "ROLE_ADMIN";
    public static final String PRIVILEGE_ADMIN = "PRIVILEGE_ADMIN";
    public static final String PROFILE_ADMIN = "PROFILE_ADMIN";
    public static final String USER = "USER";

    @Id
    @GeneratedValue
    private Long id;
    private String name;

    @ManyToMany(mappedBy = "privileges")
    private Collection<Role> roles;

    public Privilege(String name) {
        this.name = name;
    }
}
