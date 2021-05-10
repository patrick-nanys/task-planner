package com.kivik.taskplanner.repositories;

import com.kivik.taskplanner.entities.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "team", path = "team")
public interface TeamRepository extends JpaRepository<Team, Long> {
    Team findByName(String name);
}
