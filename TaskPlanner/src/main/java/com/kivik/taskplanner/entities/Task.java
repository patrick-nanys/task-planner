package com.kivik.taskplanner.entities;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Entity
@Data
public class Task {
    @Id
    @GeneratedValue
    private Long id;
    @NotBlank
    private String name;
    private String description;
    @FutureOrPresent
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime deadline;
    private Boolean deadlineReminderTriggered = false;

    @ManyToOne
    private User assignedUser;

    @OneToOne
    private Team team;
}
