package com.kivik.taskplanner.controller;

import com.kivik.taskplanner.entities.Privilege;
import com.kivik.taskplanner.entities.Task;
import com.kivik.taskplanner.entities.User;
import com.kivik.taskplanner.services.PrincipalService;
import com.kivik.taskplanner.services.TaskService;
import com.kivik.taskplanner.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Controller
@RequestMapping(path="/tasks")
public class TaskController {
    private final static Logger LOGGER = Logger.getLogger(TaskController.class.getName());

    @Autowired
    PrincipalService principalService;

    @Autowired
    TaskService taskService;

    @Autowired
    UserService userService;

    @GetMapping()
    public String getTasks(Principal principal, Model model) {
        User currentUser = principalService.getUserFromPrincipal(principal);
        List<Task> tasks = taskService.findByAssignedUser(currentUser);
        model.addAttribute("tasks", tasks);
        return "task_list";
    }

    @PostMapping()
    public String deleteTask(@Valid Long id, Principal principal) {
        User currentUser = principalService.getUserFromPrincipal(principal);
        if (!userService.userOwnsTask(currentUser, id)) {
            LOGGER.info("User " + currentUser + " tried to delete task that is not his/hers or that does not exist!");
            return "redirect:/tasks/";
        }
        taskService.deleteById(id);
        return "redirect:/tasks/";
    }

    @GetMapping("/create")
    public String getCreateTaskPage(Model model) {
        Task task = new Task();
        model.addAttribute("task", task);
        return "task_new";
    }

    @PostMapping("/create")
    public String createNewTask(@Valid Task task, BindingResult bindingResult, Model model, Principal principal) {
        if (bindingResult.hasErrors()) {
            LOGGER.log(Level.WARNING, "There was an error in the task creating process");
            List<String> messages = new ArrayList<>();
            for (ObjectError error : bindingResult.getAllErrors()) {
                if (error instanceof FieldError) {
                    FieldError fe = (FieldError) error;
                    messages.add(fe.getField() + ": " + fe.getDefaultMessage());
                }
            }

            model.addAttribute("task_new_response", messages);
            return "task_new";
        }

        User currentUser = principalService.getUserFromPrincipal(principal);
        task.setAssignedUser(currentUser);
        taskService.save(task);
        return "redirect:/tasks";
    }
}
