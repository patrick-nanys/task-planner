package com.kivik.taskplanner.services;

import com.kivik.taskplanner.entities.Task;
import com.kivik.taskplanner.entities.Team;
import com.kivik.taskplanner.entities.User;
import com.kivik.taskplanner.repositories.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

@Service
public class TaskService {
    private final static Logger LOGGER = Logger.getLogger(TaskService.class.getName());

    @Autowired
    TaskRepository taskRepo;

    @Transactional
    public Task save(Task task) {
        Task savedTask = taskRepo.save(task);
        LOGGER.info("Saved task: " + task);
        return savedTask;
    }

    public List<Task> findByAssignedUser(User user) {
        List<Task> tasks = taskRepo.findByAssignedUser(user);
        LOGGER.info("Loaded " + tasks.size() + " tasks");
        return tasks;
    }

    public List<Task> findByTeam(Team team) {
        List<Task> tasks = taskRepo.findByTeam(team);
        LOGGER.info("Loaded " + tasks.size() + " tasks");
        return tasks;
    }

    public void deleteById(Long id) {
        LOGGER.info("Deleting task with id: " + id);
        taskRepo.deleteById(id);
    }

    public Optional<Task> findById(Long id) {
        return taskRepo.findById(id);
    }

    public List<Task> findAll() {
        return taskRepo.findAll();
    }
}
