package com.kivik.taskplanner.services;

import com.kivik.taskplanner.entities.Task;
import com.kivik.taskplanner.entities.Team;
import com.kivik.taskplanner.entities.User;
import com.kivik.taskplanner.repositories.TeamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class TeamService {
    private final static Logger LOGGER = Logger.getLogger(TeamService.class.getName());

    @Autowired
    TeamRepository teamRepo;

    @Autowired
    UserService userService;

    @Transactional
    public Team save(Team team) {
        Team savedTeam = teamRepo.save(team);
        LOGGER.info("Team saved: " + savedTeam);
        return savedTeam;
    }

    public Team findById(Long id) {
        Optional<Team> team = teamRepo.findById(id);
        if (team.isEmpty()) {
            throw new RuntimeException("Team was null in findById, this should not happen!");
        }
        return team.get();
    }

    @Transactional
    public User getOwner(Long id) {
        Team team = findById(id);
        return team.getOwner();
    }

    @Transactional
    public void setOwner(Team team, User owner) {
        team.setOwner(owner);
        teamRepo.save(team);
        LOGGER.log(Level.INFO, "Updated team with an owner: " + team);
        addTeamMember(team, owner);
    }

    @Transactional
    public Team addTeamMember(Team team, User newMember) {
        // Update team with new member
        List<User> members = team.getMembers();
        if (members == null) {
            members = new ArrayList<>();
        } else {
            members = new ArrayList<>(members);
        }
        members.add(newMember);
        team.setMembers(members);

        team = teamRepo.save(team);
        LOGGER.log(Level.INFO, "Updated team with new member: " + team);

        // Update member with new team
        User loadedNewMember = userService.findByEmail(newMember.getEmail());
        List<Team> usersTeams = loadedNewMember.getTeams();
        if (usersTeams == null) {
            usersTeams = new ArrayList<>();
        } else {
            usersTeams = new ArrayList<>(usersTeams);
        }
        usersTeams.add(team);
        newMember.setTeams(usersTeams);

        userService.save(newMember);
        LOGGER.log(Level.INFO, "Updated member with new team: " + newMember);

        return team;
    }

    @Transactional
    public List<User> getMembers(Long teamId) {
        Team team = findById(teamId);
        return team.getMembers();
    }

    public List<Team> findAll() {
        return teamRepo.findAll();
    }

    public Team findByName(String teamName) {
        return teamRepo.findByName(teamName);
    }
}
