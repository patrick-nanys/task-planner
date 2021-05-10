package com.kivik.taskplanner.controller;

import com.kivik.taskplanner.entities.Role;
import com.kivik.taskplanner.entities.User;
import com.kivik.taskplanner.services.RoleService;
import com.kivik.taskplanner.services.SafeCreateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Controller
public class MainController {
    private final static Logger LOGGER = Logger.getLogger(MainController.class.getName());

    @Autowired
    RoleService roleService;

    @Autowired
    SafeCreateService safeCreateService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @GetMapping("/")
    public String getMainPage() {
        return "index";
    }

    @GetMapping("/register")
    String registerUser(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@Valid User user, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            LOGGER.log(Level.WARNING, "There was an error in the registration process");
            List<String> messages = new ArrayList<>();
            for (ObjectError error : bindingResult.getAllErrors()) {
                if (error instanceof FieldError) {
                    FieldError fe = (FieldError) error;
                    messages.add(fe.getField() + ": " + fe.getDefaultMessage());
                }
            }

            model.addAttribute("register_response", messages);
            return "register";
        }

        LOGGER.log(Level.INFO, "User trying to register with the following details:\n" +
                "Username: " + user.getEmail() + "\n" +
                "Firstname: " + user.getFirstName() + "\n" +
                "Lastname: " + user.getLastName() + "\n");

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRoles(Collections.singletonList(roleService.findByName(Role.ROLE_USER)));

        boolean userFreshlyCreated = safeCreateService.createUserIfNotFound(user);
        if (!userFreshlyCreated) {
            LOGGER.warning("User already exists!");
            model.addAttribute("register_response", "User already exists!");
            return "register";
        }
        LOGGER.info("User successfully created");
        return "register_success";
    }
}
