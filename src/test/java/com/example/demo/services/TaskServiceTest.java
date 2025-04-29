package com.example.demo.services;

import com.example.demo.TestFixtures;
import com.example.demo.dtos.task.TaskCreateDto;
import com.example.demo.dtos.task.TaskDto;
import com.example.demo.entities.TaskEntity;
import com.example.demo.entities.UserEntity;
import com.example.demo.repositories.TaskRepository;
import com.example.demo.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class TaskServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private TaskRepository taskRepository;
    @Mock
    private ModelMapper modelMapper;
    @InjectMocks
    private TaskService taskService;
    private static final UUID id = UUID.randomUUID();
    private static final String name = "tarefa";
    private static final String email = "reginaldo@gmail.com";
    private static final String password = "123";
    private static final String description = "terminar tarefa";
    private static final boolean did = false;
    private UserEntity user;
    private TaskEntity task;
    private TaskDto taskDto;
    private TaskCreateDto taskCreateDto;
    @BeforeEach
    void setup(){
        user = TestFixtures.createUserEntity(id,name,email,password);
        task = TestFixtures.createTaskEntity(id,name,description,did,user);
        taskDto = TestFixtures.createTaskDto(name,description,did,user.getId());
        taskCreateDto = TestFixtures.createTaskCreateDto(name,description);
        user.setTarefas(List.of(task));
    }

    void getTaskByUserIdAndId_;
    void getAllTasksByUserId_whenUserIdIsValidThenReturnAllTasks(){

    };
    void deleteTask_whenUserOrTaskIsNotFoundThenThrownAnTaskNotFoundException;
    void updateTask_whenUserIdAndIdIsValidThenReturnTaskDto;
    void createTask_;
    void handleTask;
}