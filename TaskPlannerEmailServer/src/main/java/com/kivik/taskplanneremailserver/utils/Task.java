package com.kivik.taskplanneremailserver.utils;

import lombok.Data;

@Data
public class Task {
    private String name;
    private String description;
    private String deadline;
    private boolean deadlineReminderTriggered;

    public Task(String name, String description, String deadline, boolean deadlineReminderTriggered) {
        this.name = name;
        this.description = description;
        this.deadline = deadline;
        this.deadlineReminderTriggered = deadlineReminderTriggered;
    }
}
