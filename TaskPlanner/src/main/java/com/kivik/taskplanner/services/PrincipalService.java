package com.kivik.taskplanner.services;

import com.kivik.taskplanner.entities.User;
import com.kivik.taskplanner.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class PrincipalService {
    private final static Logger LOGGER = Logger.getLogger(PrincipalService.class.getName());

    @Autowired
    UserRepository userRepo;

    public User getUserFromPrincipal(Principal principal) {
        String userName = principal.getName();
        User user = userRepo.findByEmail(userName);
        if (user == null) {
            throw new UsernameNotFoundException("Username " + userName + "not found!");
        }
        LOGGER.log(Level.INFO, "User " + user + " loaded from principal");
        return user;
    }
}
