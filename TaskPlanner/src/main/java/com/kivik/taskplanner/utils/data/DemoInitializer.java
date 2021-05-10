package com.kivik.taskplanner.utils.data;

import com.kivik.taskplanner.entities.Role;
import com.kivik.taskplanner.entities.Task;
import com.kivik.taskplanner.entities.Team;
import com.kivik.taskplanner.entities.User;
import com.kivik.taskplanner.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class DemoInitializer {
    @Autowired
    private UserService userService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private TeamService teamService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private SafeCreateService safeCreateService;

    private Task createDemoTask(User user, String taskName) {
        Task task = new Task();
        task.setName(taskName);
        task.setDescription("Some description of the " + taskName);
        task.setAssignedUser(user);
        return task;
    }

    public void createAndSaveDemoTask(User user, String taskName) {
        Task task = createDemoTask(user, taskName);
        taskService.save(task);
    }

    public void setupDemoTasks() {
        User demo1 = userService.findByEmail("demo");
        User demo2 = userService.findByEmail("demo2");

        createAndSaveDemoTask(demo1, "first demo task");
        createAndSaveDemoTask(demo1, "second demo task");
        createAndSaveDemoTask(demo2, "first demo task");
        createAndSaveDemoTask(demo2, "second demo task");

        Task demoTeamTask1 = createDemoTask(demo1, "first demo team task");
        List<Team> teams = userService.getTeams(demo1);
        Team team = teams.get(0);
        demoTeamTask1.setTeam(team);
        taskService.save(demoTeamTask1);

        Task demoTeamTask2 = createDemoTask(demo1, "second demo team task");
        demoTeamTask2.setTeam(userService.getTeams(demo1).get(0));
        taskService.save(demoTeamTask2);
    }

    public void setupDemoUsers() {
        User demoUser = new User();
        demoUser.setFirstName("-");
        demoUser.setLastName("-");
        demoUser.setEmail("demo@gmail.com");
        demoUser.setPassword(passwordEncoder.encode("demo"));
        demoUser.setRoles(Collections.singletonList(roleService.findByName(Role.ROLE_USER)));

        User demoUser2 = new User();
        demoUser2.setFirstName("-");
        demoUser2.setLastName("-");
        demoUser2.setEmail("demo2@gmail.com");
        demoUser2.setPassword(passwordEncoder.encode("demo2"));
        demoUser2.setRoles(Collections.singletonList(roleService.findByName(Role.ROLE_USER)));

        safeCreateService.createUserIfNotFound(demoUser);
        safeCreateService.createUserIfNotFound(demoUser2);

        Team demoTeam = new Team("Demo team");
        safeCreateService.createTeamIfNotFound(demoTeam);
        teamService.setOwner(demoTeam, demoUser);
        teamService.addTeamMember(demoTeam, demoUser2);
    }

    public void setupDemoData() {
        setupDemoUsers();
//        setupDemoTasks();
    }
}
