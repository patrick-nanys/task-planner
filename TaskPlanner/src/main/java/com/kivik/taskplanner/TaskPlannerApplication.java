package com.kivik.taskplanner;

import com.kivik.taskplanner.utils.data.DemoInitializer;
import com.kivik.taskplanner.utils.data.Initializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TaskPlannerApplication implements CommandLineRunner {

    @Autowired
    Initializer initializer;

    @Autowired
    DemoInitializer demoInitializer;

    public static void main(String[] args) {
        SpringApplication.run(TaskPlannerApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        initializer.setupRolesAndPrivileges();
        initializer.setupUsers();

        demoInitializer.setupDemoData();
    }
}
