package com.example.todolist.controller;

import org.junit.jupiter.api.Test;
import org.springframework.ui.Model;
import static org.junit.jupiter.api.Assertions.*;

class TaskControllerTest {

    @Test
    void testAddTask() {
        TaskController controller = new TaskController();
        String result = controller.addTask("Tarea de prueba");
        assertEquals("redirect:/", result);
    }

    @Test
    void testShowAddForm() {
        TaskController controller = new TaskController();
        String result = controller.showAddForm();
        assertEquals("add", result);
    }

    @Test
    void testIndex() {
        TaskController controller = new TaskController();
        Model model = new org.springframework.ui.ConcurrentModel();
        String result = controller.index(model);
        assertEquals("index", result);
        assertTrue(model.containsAttribute("tasks"));
    }
}