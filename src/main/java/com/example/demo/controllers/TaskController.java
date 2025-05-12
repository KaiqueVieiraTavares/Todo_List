package com.example.demo.controllers;


import com.example.demo.dtos.task.TaskCreateDto;
import com.example.demo.dtos.task.TaskDto;
import com.example.demo.dtos.task.TaskUpdateDto;
import com.example.demo.entities.UserEntity;

import com.example.demo.exception.userexceptions.UserNotFound;
import com.example.demo.infra.security.SecurityUtils;
import com.example.demo.repositories.TaskRepository;
import com.example.demo.repositories.UserRepository;
import com.example.demo.services.TaskService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import org.springframework.security.access.AccessDeniedException;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/todolist")
public class TaskController {
    private final TaskService taskService;
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    private final SecurityUtils securityUtils;
    public TaskController(TaskService taskService, UserRepository userRepository, TaskRepository taskRepository, SecurityUtils securityUtils) {
        this.taskService = taskService;
        this.userRepository = userRepository;
        this.taskRepository = taskRepository;
        this.securityUtils = securityUtils;
    }


    @GetMapping("/users/{userId}/tasks")
    @PreAuthorize("@securityUtils.getId() == #userId or hasAuthority('ADMIN')")
    public ResponseEntity<List<TaskDto>> getAllTasksByUserId(@PathVariable UUID userId) throws AccessDeniedException {

        List<TaskDto> tasks = taskService.getAllTasksByUserID(userId);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/users/{userId}/tasks/{taskId}")
    @PreAuthorize("@securityUtils.getId() == #userId or hasAuthority('ADMIN')")
    public ResponseEntity<TaskDto> getTaskByUserId(@PathVariable UUID userId,@PathVariable UUID taskId)  {
        TaskDto taskDto = taskService.getTask( taskId, userId);
        return ResponseEntity.ok(taskDto);
    }
    @PutMapping("/users/{userId}/tasks/{taskId}")
    @PreAuthorize("@securityUtils.getId() == #userId")
    public ResponseEntity<TaskDto> updateTaskById(@RequestBody @Valid TaskUpdateDto taskDto, @PathVariable UUID userId, @PathVariable UUID taskId) {
        TaskDto taskDto1 = taskService.updateTask(taskDto, userId, taskId);
        return ResponseEntity.ok(taskDto1);
    }
    @DeleteMapping("/users/{userId}/tasks/{taskId}")
    @PreAuthorize("@securityUtils.getId() == #userId or hasAuthority('ADMIN')")
    public ResponseEntity<Void> deleteTaskById(@PathVariable UUID userId,@PathVariable UUID taskId) throws AccessDeniedException {
        taskService.deleteTask(taskId, userId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/users/{userId}/tasks")
    @PreAuthorize("@securityUtils.getId() == #userId or hasAuthority('ADMIN')")
    public ResponseEntity<TaskDto> createTask(@RequestBody @Valid TaskCreateDto taskDto,
                                              @PathVariable UUID userId) {
       UserEntity user = userRepository.findById(userId)
               .orElseThrow(()-> new UserNotFound("Usuario nao encontrado"));
        TaskDto createdTask = taskService.createTask(taskDto, user);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTask);
    }

    @PatchMapping("/users/{userId}/tasks/{taskId}")
    @PreAuthorize("@securityUtils.getId() == #userId")
    public ResponseEntity<TaskDto> toggleDidByUserIdAndId(@PathVariable UUID userId, @PathVariable UUID taskId)  {
        TaskDto taskDto = taskService.handleTask(taskId, userId);
        return ResponseEntity.ok(taskDto);
    }
}
