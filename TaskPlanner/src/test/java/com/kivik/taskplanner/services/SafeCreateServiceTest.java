package com.kivik.taskplanner.services;

import com.kivik.taskplanner.entities.Privilege;
import com.kivik.taskplanner.entities.Role;
import com.kivik.taskplanner.entities.Team;
import com.kivik.taskplanner.entities.User;
import com.kivik.taskplanner.repositories.PrivilegeRepository;
import com.kivik.taskplanner.repositories.RoleRepository;
import com.kivik.taskplanner.repositories.TeamRepository;
import com.kivik.taskplanner.repositories.UserRepository;
import com.kivik.taskplanner.services.SafeCreateService;
import org.assertj.core.util.ArrayWrapperList;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ArrayBlockingQueue;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@WebMvcTest(SafeCreateService.class)
@ContextConfiguration(classes = SafeCreateService.class)
class SafeCreateServiceTest {

    @MockBean
    UserRepository userRepo;

    @MockBean
    PrivilegeRepository privilegeRepo;

    @MockBean
    RoleRepository roleRepo;

    @MockBean
    TeamRepository teamRepo;

    @Autowired
    SafeCreateService safeCreateService;

    @Test
    public void CreateUserIfNotFound() {
        User user = new User();
        user.setEmail("test_@gmail.com");
        assertTrue(safeCreateService.createUserIfNotFound(user));
        Mockito.verify(userRepo, Mockito.times(1)).findByEmail(user.getEmail());
        Mockito.verify(userRepo, Mockito.times(1)).save(user);

        when(userRepo.findByEmail("test_@gmail.com")).thenReturn(user);
        assertFalse(safeCreateService.createUserIfNotFound(user));
        Mockito.verify(userRepo, Mockito.times(2)).findByEmail(user.getEmail());
        Mockito.verify(userRepo, Mockito.times(1)).save(user);
    }

    @Test
    public void CreatePrivilegeIfNotFound() {
        Privilege privilege = new Privilege("test_LEADER");
        when(privilegeRepo.findByName("test_LEADER")).thenReturn(privilege);
        assertEquals(privilege, safeCreateService.createPrivilegeIfNotFound("test_LEADER"));
        Mockito.verify(privilegeRepo, Mockito.times(1)).findByName("test_LEADER");
    }

    @Test
    public void CreateRoleIfNotFound() {
        Role role = new Role("test_King");
        role.setPrivileges(new ArrayList<>());
        Collection<Privilege> privileges = new ArrayList<Privilege>();
        assertTrue(safeCreateService.createRoleIfNotFound("test_King", privileges));
        Mockito.verify(roleRepo, Mockito.times(1)).findByName("test_King");
        Mockito.verify(roleRepo, Mockito.times(1)).save(role);

        when(roleRepo.findByName("test_King")).thenReturn(role);
        assertFalse(safeCreateService.createRoleIfNotFound("test_King", privileges));
        Mockito.verify(roleRepo, Mockito.times(2)).findByName("test_King");
        Mockito.verify(roleRepo, Mockito.times(1)).save(role);
    }

    @Test
    public void CreateTeamIfNotFound() {
        Team team = new Team("test_KiVik");
        assertTrue(safeCreateService.createTeamIfNotFound(team));
        Mockito.verify(teamRepo, Mockito.times(1)).findByName("test_KiVik");
        Mockito.verify(teamRepo, Mockito.times(1)).save(team);

        when(teamRepo.findByName("test_KiVik")).thenReturn(team);
        assertFalse(safeCreateService.createTeamIfNotFound(team));
        Mockito.verify(teamRepo, Mockito.times(2)).findByName("test_KiVik");
        Mockito.verify(teamRepo, Mockito.times(1)).save(team);

    }
}
