package com.kivik.taskplanner.services;

import com.kivik.taskplanner.entities.Privilege;
import com.kivik.taskplanner.entities.Role;
import com.kivik.taskplanner.entities.Team;
import com.kivik.taskplanner.entities.User;
import com.kivik.taskplanner.repositories.PrivilegeRepository;
import com.kivik.taskplanner.repositories.RoleRepository;
import com.kivik.taskplanner.repositories.TeamRepository;
import com.kivik.taskplanner.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class SafeCreateService {
    private final static Logger LOGGER = Logger.getLogger(SafeCreateService.class.getName());

    @Autowired
    UserRepository userRepo;

    @Autowired
    PrivilegeRepository privilegeRepo;

    @Autowired
    RoleRepository roleRepo;

    @Autowired
    TeamRepository teamRepo;

    @Transactional
    public boolean createUserIfNotFound(User user) {
        User foundUser = userRepo.findByEmail(user.getEmail());
        if (foundUser == null) {
            LOGGER.log(Level.INFO, "User created with the following Email: " + user.getEmail());
            userRepo.save(user);
            return true;
        }
        LOGGER.log(Level.INFO, "User already exists with the following Email: " + user.getEmail());
        return false;
    }

    @Transactional
    public Privilege createPrivilegeIfNotFound(String name) {
        Privilege privilege = privilegeRepo.findByName(name);
        if (privilege == null) {
            privilege = new Privilege(name);
            privilegeRepo.save(privilege);
            LOGGER.log(Level.INFO, "Created privilege: " + name);
        } else {
            LOGGER.log(Level.INFO, "Privilege already exists with the following name: " + name);
        }
        return privilege;
    }

    @Transactional
    public boolean createRoleIfNotFound(String name, Collection<Privilege> privileges) {
        Role role = roleRepo.findByName(name);
        if (role == null) {
            role = new Role(name);
            role.setPrivileges(privileges);
            roleRepo.save(role);
            LOGGER.log(Level.INFO, "Created role: " + name);
            return true;
        }
        LOGGER.log(Level.INFO, "Role already exists with the following name: " + name);
        return false;
    }

    @Transactional
    public boolean createTeamIfNotFound(Team team) {
        Team foundTeam = teamRepo.findByName(team.getName());
        if (foundTeam ==  null) {
            teamRepo.save(team);
            LOGGER.log(Level.INFO, "Created team: " + team);
            return true;
        }
        LOGGER.log(Level.INFO, "Tried to create team " + team + " but team " + foundTeam + " already exists!");
        return false;
    }
}
