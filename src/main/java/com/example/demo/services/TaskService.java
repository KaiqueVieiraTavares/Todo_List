package com.example.demo.services;

import com.example.demo.dtos.task.TaskCreateDto;
import com.example.demo.dtos.task.TaskDto;
import com.example.demo.entities.TaskEntity;
import com.example.demo.entities.UserEntity;
import com.example.demo.exception.taskexceptions.TaskNotFound;
import com.example.demo.exception.userexceptions.UserNotFound;
import com.example.demo.infra.security.CustomUserDetailsService;
import com.example.demo.infra.security.SecurityFilter;
import com.example.demo.infra.security.SecurityUtils;
import com.example.demo.repositories.TaskRepository;
import com.example.demo.repositories.UserRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.UUID;

@Service
public class TaskService {
    private final ModelMapper modelMapper;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public TaskService(ModelMapper modelMapper, TaskRepository taskRepository, UserRepository userRepository) {
        this.modelMapper = modelMapper;
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }


    private TaskEntity validateTaskAcess(UUID taskId) throws AccessDeniedException {
        TaskEntity taskEntity = taskRepository.findById(taskId).orElseThrow(()-> new TaskNotFound("Tarefa nao encontrada"));
        if(!SecurityUtils.isAdmin() && !taskEntity.getUser().getId().equals((SecurityUtils.getId()))){
            throw new AccessDeniedException("Acesso negado");
        }
        return taskEntity;
    }



    public List<TaskDto> getAllTasksByUserID() {
        UUID userId = SecurityUtils.getId();
        List<TaskEntity> taskEntities = taskRepository.findByUserId(userId);
        return taskEntities.stream().map(task -> modelMapper.map(task, TaskDto.class)).toList();
    }

    public TaskDto getTask(UUID taskID) throws AccessDeniedException {
         TaskEntity taskEntity = validateTaskAcess(taskID);
        return modelMapper.map(taskEntity, TaskDto.class);
    }

    @Transactional
    public void deleteTask( UUID taskId) throws AccessDeniedException {
        taskRepository.delete(validateTaskAcess(taskId));
    }

    @Transactional
    public TaskDto updateTask(TaskDto taskDto, UUID taskId) throws AccessDeniedException {

        TaskEntity taskEntity = validateTaskAcess(taskId);
        taskEntity.setName(taskDto.getName());
        taskEntity.setDescription(taskDto.getDescription());

        return modelMapper.map(taskRepository.save(taskEntity), TaskDto.class);
    }

    // Service: Problema na conversão do User
    @Transactional
    public TaskDto createTask(TaskCreateDto taskDto) {
        UUID userID = SecurityUtils.getId();
        UserEntity user = userRepository.findById(userID).orElseThrow(()-> new UserNotFound
                ("Usuario nao encontrado"));
            TaskEntity taskEntity = new TaskEntity();
            taskEntity.setName(taskDto.getName());
            taskEntity.setDescription(taskDto.getDescription());
            taskEntity.setUser(user);
            taskEntity.setDid(false);

            TaskEntity savedTask = taskRepository.save(taskEntity);

        // Mapeamento manual (ou usando ModelMapper configurado)
        return modelMapper.map(savedTask, TaskDto.class);
    }

    @Transactional
    public TaskDto handleTask(UUID taskId) throws AccessDeniedException {
        TaskEntity taskEntity = validateTaskAcess(taskId);
        taskEntity.setDid(!taskEntity.isDid());
        return modelMapper.map(taskRepository.save(taskEntity), TaskDto.class);
    }
}

