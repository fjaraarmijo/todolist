package com.example.todolist.controller;

import com.example.todolist.model.Task;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Controller
public class TaskController {

    private static final String REDIRECT_HOME = "redirect:/";

    private final List<Task> tasks = new ArrayList<>();
    private final AtomicLong counter = new AtomicLong();

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("tasks", tasks);
        return "index";
    }

    @GetMapping("/add")
    public String showAddForm() {
        return "add";
    }

    @PostMapping("/add")
    public String addTask(@RequestParam String name) {
        tasks.add(new Task(counter.incrementAndGet(), name));
        return REDIRECT_HOME;
    }

    @PostMapping("/toggle")
    public String toggleTask(@RequestParam Long id) {
        tasks.stream()
            .filter(t -> t.getId().equals(id))
            .findFirst()
            .ifPresent(t -> t.setCompleted(!t.isCompleted()));
        return REDIRECT_HOME;
    }

    @PostMapping("/delete")
    public String deleteTask(@RequestParam Long id) {
        tasks.removeIf(t -> t.getId().equals(id));
        return REDIRECT_HOME;
    }
}
