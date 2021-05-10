package com.kivik.taskplanner.services;

import com.kivik.taskplanner.entities.Task;
import com.kivik.taskplanner.entities.Team;
import com.kivik.taskplanner.entities.User;
import com.kivik.taskplanner.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;

@Service
public class UserService {
    private final static Logger LOGGER = Logger.getLogger(UserService.class.getName());

    @Autowired
    UserRepository userRepo;

    @Transactional
    public User save(User user) {
        userRepo.save(user);
        LOGGER.info("Saved user: " + user);
        return user;
    }

    @Transactional
    public User findByEmail(String email) {
        return userRepo.findByEmail(email);
    }

    @Transactional
    public boolean userIsInTeam(User user, Long teamId) {
        List<Team> userTeams = user.getTeams();
        for (Team team : userTeams) {
            if (team.getId().equals(teamId)) {
                return true;
            }
        }
        return false;
    }

    @Transactional
    public List<Team> getTeams(User user) {
        User loadedUser = findByEmail(user.getEmail());
        List<Team> teams = loadedUser.getTeams();
        if (teams != null)
            LOGGER.info("Loaded " + teams.size() + " teams");
        else
            LOGGER.info("Loaded teams is null");
        return teams;
    }

    @Transactional
    public List<User> findUserNotInList(Collection<User> users) {
        List<User> nonMatchingUsers = userRepo.findUsersNotInList(users);
        LOGGER.info("Found " + nonMatchingUsers.size() + " non matching users");
        return nonMatchingUsers;
    }

    @Transactional
    public boolean userOwnsTask(User user, Long taskId) {
        User loadedUser = userRepo.findByEmail(user.getEmail());
        List<Task> userTasks = loadedUser.getTasks();
        for (Task t : userTasks) {
            if (t.getId().equals(taskId)) {
                return true;
            }
        }
        return false;
    }
}
