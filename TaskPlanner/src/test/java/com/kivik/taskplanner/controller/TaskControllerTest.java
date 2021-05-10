package com.kivik.taskplanner.controller;

import com.kivik.taskplanner.entities.Task;
import com.kivik.taskplanner.entities.User;
import com.kivik.taskplanner.services.TaskService;
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
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class TaskControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    TaskService taskService;

    @Autowired
    UserService userService;

    String basicDigestHeaderValue = "Basic " + new String(Base64.encodeBase64(("demo@gmail.com:demo").getBytes()));

    @Test
    void getTasks() throws Exception {
        mockMvc.perform(get("/tasks").header("Authorization", basicDigestHeaderValue))
                .andExpect(status().isOk())
                .andExpect(view().name("task_list"))
                .andExpect(model().attributeExists("tasks"));
    }

    @Test
    void getTasksUnauthorized() throws Exception {
        mockMvc.perform(get("/tasks"))
                .andExpect(status().is(401));
    }

    @Test
    void getCreate() throws Exception {
        mockMvc.perform(get("/tasks/create").header("Authorization", basicDigestHeaderValue))
                .andExpect(status().isOk())
                .andExpect(view().name("task_new"));
    }

    @Test
    void getCreateUnauthorized() throws Exception {
        mockMvc.perform(get("/tasks/create"))
                .andExpect(status().is(401));
    }

    @Test
    void postCreateNewTask() throws Exception {
        mockMvc.perform(post("/tasks/create")
                .header("Authorization", basicDigestHeaderValue)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .content(EntityUtils.toString(new UrlEncodedFormEntity(Arrays.asList(
                        new BasicNameValuePair("name", "zh"),
                        new BasicNameValuePair("description", "important"),
                        new BasicNameValuePair("deadline", "2021-05-11T15:00")
                )))))
                .andExpect(status().is(302))
                .andExpect(model().attributeDoesNotExist("task_new_response"))
                .andExpect(view().name("redirect:/tasks"));
        assertEquals(1, taskService.findAll().size());
    }

    @Test
    void postCreateNewTaskBadFormat() throws Exception {
        mockMvc.perform(post("/tasks/create")
                .header("Authorization", basicDigestHeaderValue)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .content(EntityUtils.toString(new UrlEncodedFormEntity(Arrays.asList(
                        new BasicNameValuePair("name", "zh"),
                        new BasicNameValuePair("description", "important"),
                        new BasicNameValuePair("deadline", "2021-05-11T15:00#")
                )))))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("task_new_response"))
                .andExpect(model().attributeExists("task"))
                .andExpect(view().name("task_new"));
        assertEquals(0, taskService.findAll().size());
    }

    @Test
    void postCreateNewTaskUnauthorized() throws Exception {
        mockMvc.perform(post("/tasks/create")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .content(EntityUtils.toString(new UrlEncodedFormEntity(Arrays.asList(
                        new BasicNameValuePair("name", "zh"),
                        new BasicNameValuePair("description", "important"),
                        new BasicNameValuePair("deadline", "2021-05-11T15:00")
                )))))
                .andExpect(status().is(401));
        assertEquals(0, taskService.findAll().size());
    }

    @Test
    void testCannotDeleteTaskThatIsNotOwnedByUser() throws  Exception {
        Task task = new Task();
        task.setName("Test task name");
        task = taskService.save(task);
        mockMvc.perform(post("/tasks")
                .header("Authorization", basicDigestHeaderValue)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .content(EntityUtils.toString(new UrlEncodedFormEntity(Collections.singletonList(
                        new BasicNameValuePair("id", String.valueOf(task.getId()))
                )))))
                .andExpect(status().is(302))
                .andExpect(view().name("redirect:/tasks/"));
        assertTrue(taskService.findById(task.getId()).isPresent());
    }

    @Test
    void testDeleteTask() throws  Exception {
        User demoUser = userService.findByEmail("demo@gmail.com");
        Task task = new Task();
        task.setName("Test task name");
        task.setAssignedUser(demoUser);
        task = taskService.save(task);
        mockMvc.perform(post("/tasks")
                .header("Authorization", basicDigestHeaderValue)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .content(EntityUtils.toString(new UrlEncodedFormEntity(Collections.singletonList(
                        new BasicNameValuePair("id", String.valueOf(task.getId()))
                )))))
                .andExpect(status().is(302))
                .andExpect(view().name("redirect:/tasks/"));
        assertFalse(taskService.findById(task.getId()).isPresent());
    }

    @Test
    void testDeleteTaskUnauthorized() throws  Exception {
        User demoUser = userService.findByEmail("demo@gmail.com");
        Task task = new Task();
        task.setName("Test task name");
        task.setAssignedUser(demoUser);
        task = taskService.save(task);
        mockMvc.perform(post("/tasks")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .content(EntityUtils.toString(new UrlEncodedFormEntity(Collections.singletonList(
                        new BasicNameValuePair("id", String.valueOf(task.getId()))
                )))))
                .andExpect(status().is(401));
        assertTrue(taskService.findById(task.getId()).isPresent());
    }
}