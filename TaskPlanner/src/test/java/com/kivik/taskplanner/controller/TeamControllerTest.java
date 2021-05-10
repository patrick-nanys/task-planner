package com.kivik.taskplanner.controller;

import com.kivik.taskplanner.entities.Team;
import com.kivik.taskplanner.entities.User;
import com.kivik.taskplanner.services.TaskService;
import com.kivik.taskplanner.services.TeamService;
import com.kivik.taskplanner.services.UserService;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.tomcat.util.codec.binary.Base64;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class TeamControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    TeamService teamService;

    @Autowired
    TaskService taskService;

    @Autowired
    UserService userService;

    String basicDigestHeaderValue = "Basic " + new String(Base64.encodeBase64(("demo@gmail.com:demo").getBytes()));

    @Test
    void getListUsersTeam() throws Exception {
        mockMvc.perform(get("/teams").header("Authorization", basicDigestHeaderValue))
                .andExpect(status().isOk())
                .andExpect(view().name("team_list"));
    }

    @Test
    void getListUsersTeamUnauthorized() throws Exception {
        mockMvc.perform(get("/teams"))
                .andExpect(status().is(401));
    }

    @Test
    void getCreateTeamPage() throws Exception {
        mockMvc.perform(get("/teams/create").header("Authorization", basicDigestHeaderValue))
                .andExpect(status().isOk())
                .andExpect(view().name("team_new"));
    }

    @Test
    void getCreateTeamPageUnauthorized() throws Exception {
        mockMvc.perform(get("/teams/create"))
                .andExpect(status().is(401));
    }

    @Test
    void postCreateNewTeam() throws Exception {
        mockMvc.perform(post("/teams/create")
                .header("Authorization", basicDigestHeaderValue)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .content(EntityUtils.toString(new UrlEncodedFormEntity(Arrays.asList(
                        new BasicNameValuePair("name", "KiVik team")
                )))))
                .andExpect(status().is(302))
                .andExpect(view().name("redirect:/teams"));
        assertNotNull(teamService.findByName("KiVik team"));
    }

    @Test
    void postCreateNewTeamAlreadyExists() throws Exception {
        String teamName = "Amazing kiVik team";
        Team team = new Team(teamName);
        teamService.save(team);
        mockMvc.perform(post("/teams/create")
                .header("Authorization", basicDigestHeaderValue)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .content(EntityUtils.toString(new UrlEncodedFormEntity(Arrays.asList(
                        new BasicNameValuePair("name", teamName)
                )))))
                .andExpect(status().isOk())
                .andExpect(view().name("team_new"));
        assertNotNull(teamService.findByName(teamName));
    }

    @Test
    void postCreateNewTeamUnauthorized() throws Exception {
        mockMvc.perform(post("/teams/create")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .content(EntityUtils.toString(new UrlEncodedFormEntity(Arrays.asList(
                        new BasicNameValuePair("name", "KiVik team")
                )))))
                .andExpect(status().is(401));
    }

    @Test
    void getListTeamTasks() throws Exception{
        Team team = new Team("KiVik");
        team = teamService.save(team);
        User user = userService.findByEmail("demo@gmail.com");
        teamService.addTeamMember(team, user);
        mockMvc.perform( get("/teams/" + team.getId()).header("Authorization", basicDigestHeaderValue))
                .andExpect(status().isOk())
                .andExpect(view().name("team_task_list"));
    }

    @Test
    void getListTeamTasksUnauthorized() throws Exception{
        Team team = new Team("KiVik");
        team = teamService.save(team);
        User user = userService.findByEmail("demo@gmail.com");
        teamService.addTeamMember(team, user);
        mockMvc.perform(get("/teams/" + team.getId()))
                .andExpect(status().is(401));
    }

    @Test
    void getCreateTeamTaskPage() throws Exception {
        User demoUser = userService.findByEmail("demo@gmail.com");
        Team team = new Team("KiVik");
        team.setOwner(demoUser);
        teamService.addTeamMember(team, demoUser);
        team = teamService.save(team);
        mockMvc.perform( get("/teams/"+ team.getId() + "/create").header("Authorization", basicDigestHeaderValue))
                .andExpect(status().isOk())
                .andExpect(view().name("team_task_new"));
    }

    @Test
    void getCreateTeamTaskPageUnauthorized() throws Exception {
        User demoUser = userService.findByEmail("demo@gmail.com");
        Team team = new Team("KiVik");
        team.setOwner(demoUser);
        teamService.addTeamMember(team, demoUser);
        team = teamService.save(team);
        mockMvc.perform(get("/teams/"+ team.getId() + "/create"))
                .andExpect(status().is(401));
    }

    @Test
    void postCreateTeamTaskPage() throws Exception {
        User demoUser = userService.findByEmail("demo@gmail.com");
        Team team = new Team("KiVik");
        teamService.setOwner(team, demoUser);
        team = teamService.save(team);
        mockMvc.perform(post("/teams/" + team.getId() + "/create")
                .header("Authorization", basicDigestHeaderValue)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .content(EntityUtils.toString(new UrlEncodedFormEntity(Arrays.asList(
                        new BasicNameValuePair("name", "Zh"),
                        new BasicNameValuePair("description", "important task"),
                        new BasicNameValuePair("deadline", "2021-05-11T15:00")
                )))))
                .andExpect(status().is(302))
                .andExpect(view().name("redirect:/teams/" + team.getId()));
        assertEquals(1, taskService.findAll().size());
    }

    @Test
    void postCreateTeamTaskPageUnauthorized() throws Exception {
        User demoUser = userService.findByEmail("demo@gmail.com");
        Team team = new Team("KiVik");
        teamService.setOwner(team, demoUser);
        team = teamService.save(team);
        mockMvc.perform(post("/teams/" + team.getId() + "/create")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .content(EntityUtils.toString(new UrlEncodedFormEntity(Arrays.asList(
                        new BasicNameValuePair("name", "Zh"),
                        new BasicNameValuePair("description", "important task"),
                        new BasicNameValuePair("deadline", "2021-05-11T15:00")
                )))))
                .andExpect(status().is(401));
        assertEquals(0, taskService.findAll().size());
    }

    @Test
    void getListUsersToAdd() throws Exception {
        User demoUser = userService.findByEmail("demo@gmail.com");
        Team team = new Team("KiVik");
        teamService.setOwner(team, demoUser);
        team = teamService.save(team);
        mockMvc.perform( get("/teams/"+ team.getId() + "/add_member").header("Authorization", basicDigestHeaderValue))
                .andExpect(status().isOk())
                .andExpect(view().name("peoples_without_team"));
    }

    @Test
    void getListUsersToAddUnauthorized() throws Exception {
        User demoUser = userService.findByEmail("demo@gmail.com");
        Team team = new Team("KiVik");
        teamService.setOwner(team, demoUser);
        team = teamService.save(team);
        mockMvc.perform(get("/teams/"+ team.getId() + "/add_member"))
                .andExpect(status().is(401));
    }

    @Test
    void postAddMemberToTeamUserNotInTeam() throws Exception {
        User demoUser = userService.findByEmail("demo@gmail.com");
        Team team = new Team("KiVik");
        team.setOwner(demoUser);
        team = teamService.save(team);
        mockMvc.perform(post("/teams/" + team.getId() + "/add_member")
                .header("Authorization", basicDigestHeaderValue)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .content(EntityUtils.toString(new UrlEncodedFormEntity(Arrays.asList(
                        new BasicNameValuePair("email", "demo@gmail.com")
                )))))
                .andExpect(status().is(302))
                .andExpect(view().name("redirect:/teams/"));
    }

    @Test
    void postAddMemberToTeamUserInTeam() throws Exception {
        User demoUser = userService.findByEmail("demo@gmail.com");
        Team team = new Team("KiVik");
        teamService.setOwner(team, demoUser);
        team = teamService.save(team);
        mockMvc.perform(post("/teams/" + team.getId() + "/add_member")
                .header("Authorization", basicDigestHeaderValue)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .content(EntityUtils.toString(new UrlEncodedFormEntity(Arrays.asList(
                        new BasicNameValuePair("email", "demo2@gmail.com")
                )))))
                .andExpect(status().is(302))
                .andExpect(view().name("redirect:/teams/" + team.getId() + "/add_member"));
    }

    @Test
    void postAddMemberToTeamUserInTeamUnauthorized() throws Exception {
        User demoUser = userService.findByEmail("demo@gmail.com");
        Team team = new Team("KiVik");
        teamService.setOwner(team, demoUser);
        team = teamService.save(team);
        mockMvc.perform(post("/teams/" + team.getId() + "/add_member")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .content(EntityUtils.toString(new UrlEncodedFormEntity(Arrays.asList(
                        new BasicNameValuePair("email", "demo2@gmail.com")
                )))))
                .andExpect(status().is(401));
    }
}