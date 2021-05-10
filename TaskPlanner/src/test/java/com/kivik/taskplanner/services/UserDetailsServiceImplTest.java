package com.kivik.taskplanner.services;

import com.kivik.taskplanner.entities.User;
import com.kivik.taskplanner.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@WebMvcTest(UserDetailsServiceImpl.class)
@ContextConfiguration(classes = UserDetailsServiceImpl.class)
class UserDetailsServiceImplTest {

    @MockBean
    UserRepository userRepo;

    @Autowired
    UserDetailsServiceImpl userDetailsImpl;

    @Test
    void loadUserByUsername() {
        User user = new User();
        user.setEmail("test_@gmail.com");
        when(userRepo.findByEmail("test_@gmail.com")).thenReturn(user);
        assertEquals("test_@gmail.com", userDetailsImpl.loadUserByUsername("test_@gmail.com").getUsername());
        Mockito.verify(userRepo, Mockito.times(1)).findByEmail("test_@gmail.com");
    }
}