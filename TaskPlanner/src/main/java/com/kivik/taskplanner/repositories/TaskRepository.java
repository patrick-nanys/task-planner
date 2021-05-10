package com.kivik.taskplanner.repositories;

import com.kivik.taskplanner.entities.Task;
import com.kivik.taskplanner.entities.Team;
import com.kivik.taskplanner.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource(collectionResourceRel = "task", path = "task")
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByAssignedUser(User assignee);
    List<Task> findByTeam(Team team);
}
