package com.example.demo.integration.Task;


import com.example.demo.TestFixtures;
import com.example.demo.dtos.task.TaskCreateDto;
import com.example.demo.dtos.task.TaskDto;
import com.example.demo.dtos.task.TaskUpdateDto;
import com.example.demo.entities.TaskEntity;
import com.example.demo.entities.UserEntity;
import com.example.demo.repositories.TaskRepository;
import com.example.demo.repositories.UserRepository;
import com.example.demo.services.TaskService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;


@ActiveProfiles("test")
@SpringBootTest
public class TaskServiceIntegrationTest {
    @Autowired
    private TaskService taskService;
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private UserRepository userRepository;
    private UserEntity user;
    private String name;
    private String email;
    private String password;
    private String description;
    private boolean did;
    private TaskEntity taskEntity;
    @BeforeEach
    void setup(){
        name = "tarefa";
        email = "reginaldo@gmail.com";
        password = "123";
        description = "terminar tarefa";
        did = false;
        user = TestFixtures.createUserEntity(null,name,email,password);
        userRepository.saveAndFlush(user);
        //nao inicializei a task porque esta em conflito com o name unique
    }

    @Test
    void createTask_shouldPersistInDataBase(){

        TaskCreateDto taskCreateDto = TestFixtures.createTaskCreateDto(name, description);

        TaskDto result = taskService.createTask(taskCreateDto, user);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo(taskCreateDto.getName());
        assertThat(result.getDescription()).isEqualTo(taskCreateDto.getDescription());


        Optional<TaskEntity> savedTask = taskRepository.findById(result.getTaskId());

        assertThat(savedTask).isPresent();
        assertThat(savedTask.get().getName()).isEqualTo(result.getName());
    }

    @Test
    void getTask_shouldReturnSavedTaskWhenGet() {
        // Cria e salva a tarefa (apenas uma vez)
        TaskEntity task = TestFixtures.createTaskEntity(null, name, description, did, user);
        TaskEntity savedTask = taskRepository.saveAndFlush(task);


        // Busca a tarefa via service
        TaskDto result = taskService.getTask(user.getId(), savedTask.getId());

        // Verificações
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo(savedTask.getName());
        assertThat(result.getDescription()).isEqualTo(savedTask.getDescription());
    }
    @Test
    @Transactional
    void deleteTask_shouldDeleteTask(){
        TaskEntity task = TestFixtures.createTaskEntity(null, name, description, did, user);
        TaskEntity savedTask = taskRepository.saveAndFlush(task);
        taskService.deleteTask(user.getId(), savedTask.getId());
        Optional<TaskEntity> result = taskRepository.findById(savedTask.getId());


        assertThat(result).isNotPresent();
    }
    @Test
    @Transactional
    void updateTask_shouldModifyTaskWhenPut(){
        TaskEntity task = TestFixtures.createTaskEntity(null,name,description,did,user);
        TaskEntity savedTask = taskRepository.saveAndFlush(task);
        TaskUpdateDto taskUpdateDto = TestFixtures.createTaskUpdateDto("outra tarefa", "terminar hoje");

        TaskDto result = taskService.updateTask(taskUpdateDto, user.getId(), savedTask.getId());


        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo(taskUpdateDto.getName());
        assertThat(result.getDescription()).isEqualTo(taskUpdateDto.getDescription());
    }
    @Test
    @Transactional
    void getAllTasks_shouldReturnAllTasksAndPersist(){
        TaskEntity task = TestFixtures.createTaskEntity(null, name, description, did, user);
        taskRepository.saveAndFlush(task);
        TaskEntity task2 = TestFixtures.createTaskEntity(null, "outra tarefa", "terminar hoje", did, user);
        taskRepository.saveAndFlush(task2);
        List<TaskDto> tarefas = taskService.getAllTasksByUserID(user.getId());

        assertThat(tarefas).isNotNull();
        assertThat(tarefas).hasSize(2);
        assertThat(tarefas).extracting(TaskDto::getName).containsExactlyInAnyOrder(task.getName(), task2.getName());
    }

    @Test
    @Transactional
    void patchTask_shouldModifyDidTask(){
        TaskEntity task = TestFixtures.createTaskEntity(null, name, description, did, user);
        TaskEntity savedTask = taskRepository.saveAndFlush(task);

        TaskDto result = taskService.handleTask(user.getId(), savedTask.getId());


        assertThat(result.isDid()).isTrue();
    }
}
