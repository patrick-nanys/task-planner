package com.kivik.taskplanner.controller;

import com.kivik.taskplanner.entities.Task;
import com.kivik.taskplanner.entities.Team;
import com.kivik.taskplanner.entities.User;
import com.kivik.taskplanner.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Controller
@RequestMapping(path="/teams")
public class TeamController {
    private final static Logger LOGGER = Logger.getLogger(TeamController.class.getName());

    @Autowired
    PrincipalService principalService;

    @Autowired
    UserService userService;

    @Autowired
    TaskService taskService;

    @Autowired
    TeamService teamService;

    @Autowired
    SafeCreateService safeCreateService;


    @GetMapping()
    String listUsersTeams(Principal principal, Model model) {
        User currentUser = principalService.getUserFromPrincipal(principal);
        List<Team> teams = userService.getTeams(currentUser);
        model.addAttribute("teams", teams);
        return "team_list";
    }

    @GetMapping("/create")
    String getCreateTeamPage(Model model) {
        Team team = new Team();
        model.addAttribute("team", team);
        return "team_new";
    }

    @PostMapping("/create")
    String createNewTeam(@Valid Team team, Principal principal, Model model) {
        // TODO maybe add binding results
        User currentUser = principalService.getUserFromPrincipal(principal);

        boolean teamFreshlyCreated = safeCreateService.createTeamIfNotFound(team);
        if (!teamFreshlyCreated) {
            LOGGER.warning("Team already exists!");
            model.addAttribute("team_new_response", "Team with that name already exists!");
            return "team_new";
        }
        teamService.setOwner(team, currentUser);
        return "redirect:/teams";
    }

    @GetMapping("/{team_id}")
    String listTeamTasks(@PathVariable(name = "team_id") Long teamId, Principal principal, Model model) {
        User currentUser = principalService.getUserFromPrincipal(principal);
        if (!userService.userIsInTeam(currentUser, teamId)) {
            return "redirect:/teams";
        }

        Team team = teamService.findById(teamId);
        model.addAttribute("team", team);

        List<Task> teamTasks = taskService.findByTeam(team);
        model.addAttribute("tasks", teamTasks);

        List<User> users = teamService.getMembers(teamId);
        model.addAttribute("users", users);

        return "team_task_list";
    }

    @GetMapping("/{team_id}/create")
    String getCreateTeamTaskPage(@PathVariable(name = "team_id") Long teamId, Principal principal, Model model) {
        User currentUser = principalService.getUserFromPrincipal(principal);
        if (!userService.userIsInTeam(currentUser, teamId)) {
            return "redirect:/teams";
        }
        model.addAttribute("task", new Task());
        return "team_task_new";
    }

    @PostMapping("/{team_id}/create")
    String createNewTeamTask(@PathVariable(name = "team_id") Long teamId, @Valid Task task, BindingResult bindingResult, Model model, Principal principal) {

        if (bindingResult.hasErrors()) {
            LOGGER.log(Level.WARNING, "There was an error in the task creating process");
            List<String> messages = new ArrayList<>();
            for (ObjectError error : bindingResult.getAllErrors()) {
                if (error instanceof FieldError) {
                    FieldError fe = (FieldError) error;
                    messages.add(fe.getField() + ": " + fe.getDefaultMessage());
                }
            }
            model.addAttribute("team_task_new_response", messages);
            return "team_task_new";
        }

        User currentUser = principalService.getUserFromPrincipal(principal);
        if (!userService.userIsInTeam(currentUser, teamId)) {
            return "redirect:/teams";
        }

        Team team = teamService.findById(teamId);
        task.setAssignedUser(currentUser);
        task.setTeam(team);
        taskService.save(task);

        return "redirect:/teams/" + teamId;
    }

    // TODO add member not tested yet
    @GetMapping("/{team_id}/add_member")
    String listUsersToAdd(@PathVariable(name = "team_id") Long teamId, Principal principal, Model model) {
        User currentUser = principalService.getUserFromPrincipal(principal);
        if (!userService.userIsInTeam(currentUser, teamId))
            return "redirect:/teams/";
        if (!teamService.getOwner(teamId).equals(currentUser))
            return "redirect:/teams/" + teamId;

        List<User> teamMembers = teamService.getMembers(teamId);
        // exclude admin and super_admin from the list
        List<User> usersToExclude = new ArrayList<>(Arrays.asList(
                userService.findByEmail("admin"),
                userService.findByEmail("super_admin")));
        usersToExclude.addAll(teamMembers);
        List<User> nonTeamMembers = userService.findUserNotInList(usersToExclude);
        model.addAttribute("users", nonTeamMembers);
        return "peoples_without_team";
    }

    @PostMapping("/{team_id}/add_member")
    String addMemberToTeam(@PathVariable(name = "team_id") Long teamId, @Valid String email, Principal principal) {
        User currentUser = principalService.getUserFromPrincipal(principal);
        if (!userService.userIsInTeam(currentUser, teamId))
            return "redirect:/teams/";
        if (!teamService.getOwner(teamId).equals(currentUser))
            return "redirect:/teams/" + teamId;

        User user = userService.findByEmail(email);
        Team team = teamService.findById(teamId);
        teamService.addTeamMember(team, user);
        return "redirect:/teams/" + teamId + "/add_member";
    }
}
