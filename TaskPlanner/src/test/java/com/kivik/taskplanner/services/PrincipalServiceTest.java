package com.kivik.taskplanner.services;

import com.kivik.taskplanner.entities.User;
import com.kivik.taskplanner.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.security.Principal;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@WebMvcTest(PrincipalService.class)
@ContextConfiguration(classes = PrincipalService.class)
class PrincipalServiceTest {

    @MockBean
    UserRepository userRepo;

    @Autowired
    PrincipalService principalService;

    @Test
    void getUserFromPrincipal() {
        User user = new User();
        user.setEmail("test_@gmail.com");
        Principal principal = mock(Principal.class);
        when(userRepo.findByEmail("test_@gmail.com")).thenReturn(user);
        when(principal.getName()).thenReturn("test_@gmail.com");
        assertEquals(user, principalService.getUserFromPrincipal(principal));
    }
}