package com.kivik.taskplanner.services;

import com.kivik.taskplanner.entities.Team;
import com.kivik.taskplanner.entities.User;
import com.kivik.taskplanner.repositories.TeamRepository;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@WebMvcTest(TeamService.class)
@ContextConfiguration(classes = TeamService.class)
class TeamServiceTest {

    @MockBean
    TeamRepository teamRepo;

    @MockBean
    UserService userService;

    @Autowired
    TeamService teamService;

    @Test
    void save() {
        Team team = new Team("test_KiVik");
        when(teamRepo.save(team)).thenReturn(team);
        Team savedTeam = teamService.save(team);
        assertEquals("test_KiVik", savedTeam.getName());
        Mockito.verify(teamRepo, Mockito.times(1)).save(team);
    }

    @Test
    void addTeamMember() {
        Team team = new Team("test_KiVik");
        User user = new User();
        user.setEmail("test_@gmail.com");

        when(userService.findByEmail(user.getEmail())).thenReturn(user);
        when(teamRepo.save(team)).thenReturn(team);

        Team saveTeam = teamService.addTeamMember(team, user);
        assertEquals(1, saveTeam.getMembers().size());
    }
}