package com.kivik.taskplanner.services;

import com.kivik.taskplanner.config.SecurityConfig;
import com.kivik.taskplanner.entities.User;
import com.kivik.taskplanner.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    UserRepository userRepo;
    private final static Logger LOGGER = Logger.getLogger(UserDetailsServiceImpl.class.getName());


    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepo.findByEmail(username);
        if (user == null) {
            throw new UsernameNotFoundException("Username " + username + "not found!");
        }
        LOGGER.log(Level.INFO, "User found and loaded");
        return new UserDetailsImpl(user);
    }
}
