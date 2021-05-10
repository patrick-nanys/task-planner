package com.kivik.taskplanner.services;

import com.kivik.taskplanner.entities.Task;
import com.kivik.taskplanner.entities.Team;
import com.kivik.taskplanner.entities.User;
import com.kivik.taskplanner.repositories.TaskRepository;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;


@RunWith(SpringRunner.class)
@WebMvcTest(TaskService.class)
@ContextConfiguration(classes = TaskService.class)
class TaskServiceTest {

    @MockBean
    TaskRepository taskRepo;

    @Autowired
    TaskService taskService;

    @Test
    void save() {
        Task task = new Task();
        task.setName("test task");
        when(taskRepo.save(task)).thenReturn(task);
        task = taskService.save(task);
        assertEquals("test task", task.getName());
        Mockito.verify(taskRepo, Mockito.times(1)).save(task);
    }

    @Test
    void findByAssignedUser() {
        User user = new User();
        user.setEmail("test_@gmail.com");
        Task task = new Task();
        user.setTasks(Collections.singletonList(task));
        List<Task> tasks = new ArrayList<Task>();
        when(taskRepo.findByAssignedUser(user)).thenReturn(Collections.singletonList(task));
        tasks = taskService.findByAssignedUser(user);
        assertEquals(1, tasks.size());
    }

    @Test
    void findByTeam() {
        Team team = new Team("test_KiVik");
        Task task = new Task();
        task.setId(2l);
        when(taskRepo.findByTeam(team)).thenReturn(Collections.singletonList(task));
        assertEquals(task, taskService.findByTeam(team).get(0));
        Mockito.verify(taskRepo, Mockito.times(1)).findByTeam(team);
    }

}