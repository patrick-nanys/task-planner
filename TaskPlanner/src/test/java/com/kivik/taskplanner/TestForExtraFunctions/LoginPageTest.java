package com.kivik.taskplanner.TestForExtraFunctions;


import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class LoginPageTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    void GetLogin() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders.get("/login"))
                .andExpect(status().isOk());
    }

}
