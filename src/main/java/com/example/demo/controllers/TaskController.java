package com.example.demo.controllers;


import com.example.demo.dtos.task.TaskCreateDto;
import com.example.demo.dtos.task.TaskDto;
import com.example.demo.dtos.user.UserDto;
import com.example.demo.entities.TaskEntity;
import com.example.demo.entities.UserEntity;
import com.example.demo.infra.security.SecurityUtils;
import com.example.demo.repositories.TaskRepository;
import com.example.demo.repositories.UserRepository;
import com.example.demo.services.TaskService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/todolist")
public class TaskController {
    private final TaskService taskService;
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;

    public TaskController(TaskService taskService, UserRepository userRepository, TaskRepository taskRepository) {
        this.taskService = taskService;
        this.userRepository = userRepository;
        this.taskRepository = taskRepository;
    }

    private void checkUserIdParam(UUID userId) throws AccessDeniedException {
        if(!userId.equals(SecurityUtils.getId())){
            throw new AccessDeniedException("Acesso negado");
        }
    }
    @GetMapping("/users/{userId}/tasks")
    public ResponseEntity<List<TaskDto>> getAllTasks(@PathVariable UUID userId) throws AccessDeniedException {
        checkUserIdParam(userId);
        List<TaskDto> tasks = taskService.getAllTasksByUserID();
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/users/{userId}/tasks/{taskId}")
    public ResponseEntity<TaskDto> getTaskByUserId(@PathVariable UUID userId,@PathVariable UUID taskId) throws AccessDeniedException {
        checkUserIdParam(userId);
        TaskDto taskDto = taskService.getTask( taskId);
        return ResponseEntity.ok(taskDto);
    }
    @PutMapping("/users/{userId}/tasks/{taskId}")
    public ResponseEntity<TaskDto> updateTaskById(@RequestBody TaskDto taskDto,@PathVariable UUID userId, @PathVariable UUID taskId) throws AccessDeniedException {
        checkUserIdParam(userId);
        TaskDto taskDto1 = taskService.updateTask(taskDto, taskId);
        return ResponseEntity.ok(taskDto1);
    }
    @DeleteMapping("/users/{userId}/tasks/{taskId}")
    public ResponseEntity<Void> deleteTaskById(@PathVariable UUID userId,@PathVariable UUID taskId) throws AccessDeniedException {
        checkUserIdParam(userId);
        taskService.deleteTask(taskId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/users/{userId}/tasks")
    public ResponseEntity<TaskDto> createTask(@Valid @RequestBody TaskCreateDto taskDto ){
        TaskDto createdTask = taskService.createTask(taskDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTask);
    }

    @PatchMapping("/users/{userId}/tasks/{taskId}")
    public ResponseEntity<TaskDto> toggleDidByUserIdAndId(@PathVariable UUID userId, @PathVariable UUID taskId) throws AccessDeniedException {
        checkUserIdParam(userId);
        TaskDto taskDto = taskService.handleTask(taskId);
        return ResponseEntity.ok(taskDto);
    }
}
