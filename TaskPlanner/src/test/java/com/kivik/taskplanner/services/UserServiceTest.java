package com.kivik.taskplanner.services;

import com.kivik.taskplanner.entities.Team;
import com.kivik.taskplanner.entities.User;
import com.kivik.taskplanner.repositories.UserRepository;
import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.lang.reflect.Array;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@WebMvcTest(UserService.class)
@ContextConfiguration(classes = UserService.class)
class UserServiceTest {

    @MockBean
    UserRepository userRepo;

    @Autowired
    UserService userService;


    @Test
    void save() {
        User user = new User();
        user.setEmail("test@gmail.com");
        User savedUser = userService.save(user);
        assertEquals("test@gmail.com", savedUser.getEmail());
        Mockito.verify(userRepo, Mockito.times(1)).save(user);
    }

    @Test
    void userIsInTeam() {
        long id = 2l;
        User user = new User();
        Team team = new Team("test_KiVik");
        team.setId(id);
        user.setTeams(Collections.singletonList(team));
        assertEquals(true, userService.userIsInTeam(user, id));

    }

    @Test
    void getTeams() {
        User user = new User();
        user.setEmail("test_@gmail.com");
        when(userRepo.findByEmail("test_@gmail.com")).thenReturn(user);
        Team team = new Team("test_KiVik");
        user.setTeams(Collections.singletonList(team));
        assertEquals(1, userService.getTeams(user).size());
        Mockito.verify(userRepo, Mockito.times(1)).findByEmail(user.getEmail());
    }

    @Test
    void findUserNotInList() {
        User user = new User();
        user.setEmail("test_@gmail.com");
        List<User> users = new ArrayList<User>();
        users.add(user);
        assertEquals(0, userService.findUserNotInList(users).size());
        Mockito.verify(userRepo, Mockito.times(1)).findUsersNotInList(users);
    }
}