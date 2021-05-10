package com.kivik.taskplanner.entities;

import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.util.List;

@Entity
@Data
public class User {
    @Id
    @GeneratedValue
    private Long id;
    @NotBlank
    private String firstName;
    @NotBlank
    private String lastName;
    @Email
    private String email;
    @ToString.Exclude
    //at least 8 characters, at least one number and both lower and uppercase letters and special characters
    @Pattern(regexp = "(?=^.{8,}$)((?=.*\\d)|(?=.*\\W+))(?![.\n])(?=.*[A-Z])(?=.*[a-z]).*$", flags = Pattern.Flag.UNICODE_CASE)
    private String password;

    @ToString.Exclude
    @ManyToMany(fetch = FetchType.EAGER)
    private List<Role> roles;

    @ToString.Exclude
    @OneToMany(mappedBy = "assignedUser")
    private List<Task> tasks;

    @ToString.Exclude
    @ManyToMany
    private List<Team> teams;
}
