package com.kivik.taskplanner.controller;

import com.kivik.taskplanner.entities.User;
import com.kivik.taskplanner.services.UserService;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.tomcat.util.codec.binary.Base64;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MockMvcBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import javax.validation.Valid;
import java.net.http.HttpRequest;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class MainControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    UserService userService;

    @Test
    void getRegister() throws Exception {
        mockMvc.perform(
                get("/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().attributeExists("user"));
    }

    @Test
    void getPer() throws Exception {
        mockMvc.perform(
                get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"));
    }

    @Test
    void postRegister() throws Exception {
        mockMvc.perform(post("/register")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .content(EntityUtils.toString(new UrlEncodedFormEntity(Arrays.asList(
                        new BasicNameValuePair("email", "test@gmail.com"),
                        new BasicNameValuePair("firstName", "test"),
                        new BasicNameValuePair("lastName", "test"),
                        new BasicNameValuePair("password", "asdf123.@A")
                )))))
                .andExpect(status().isOk())
                .andExpect(view().name("register_success"));
    }

    @Test
    void postRegisterEmailExists() throws Exception {
        User user = new User();
        user.setEmail("test@gmail.com");
        user.setFirstName("test");
        user.setLastName("test");
        userService.save(user);
        mockMvc.perform(post("/register")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .content(EntityUtils.toString(new UrlEncodedFormEntity(Arrays.asList(
                        new BasicNameValuePair("email", "test@gmail.com"),
                        new BasicNameValuePair("firstName", "test"),
                        new BasicNameValuePair("lastName", "test"),
                        new BasicNameValuePair("password", "asdf123.@A")
                )))))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("register_response"))
                .andExpect(view().name("register"));
    }

    @Test
    void postRegisterBadEmailFormat() throws Exception {
        mockMvc.perform(post("/register")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .content(EntityUtils.toString(new UrlEncodedFormEntity(Arrays.asList(
                        new BasicNameValuePair("email", "test"),
                        new BasicNameValuePair("firstName", "test"),
                        new BasicNameValuePair("lastName", "test"),
                        new BasicNameValuePair("password", "asdf123.@A")
                )))))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("register_response"))
                .andExpect(view().name("register"));
    }

    @Test
    void postRegisterBadPasswordFormat() throws Exception {
        mockMvc.perform(post("/register")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .content(EntityUtils.toString(new UrlEncodedFormEntity(Arrays.asList(
                        new BasicNameValuePair("email", "test@gmail.com"),
                        new BasicNameValuePair("firstName", "test"),
                        new BasicNameValuePair("lastName", "test"),
                        new BasicNameValuePair("password", "test")
                )))))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("register_response"))
                .andExpect(view().name("register"));
    }

    @Test
    void postRegisterFirstNameBlank() throws Exception {
        mockMvc.perform(post("/register")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .content(EntityUtils.toString(new UrlEncodedFormEntity(Arrays.asList(
                        new BasicNameValuePair("email", "test@gmail.com"),
                        new BasicNameValuePair("firstName", ""),
                        new BasicNameValuePair("lastName", "test"),
                        new BasicNameValuePair("password", "asdf123.@A")
                )))))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("register_response"))
                .andExpect(view().name("register"));
    }

    @Test
    void postRegisterLastNameBlank() throws Exception {
        mockMvc.perform(post("/register")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .content(EntityUtils.toString(new UrlEncodedFormEntity(Arrays.asList(
                        new BasicNameValuePair("email", "test@gmail.com"),
                        new BasicNameValuePair("firstName", "test"),
                        new BasicNameValuePair("lastName", ""),
                        new BasicNameValuePair("password", "asdf123.@A")
                )))))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("register_response"))
                .andExpect(view().name("register"));
    }
}