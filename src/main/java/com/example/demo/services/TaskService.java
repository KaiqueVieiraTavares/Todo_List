package com.example.demo.services;

import com.example.demo.dtos.task.TaskCreateDto;
import com.example.demo.dtos.task.TaskDto;
import com.example.demo.dtos.task.TaskUpdateDto;
import com.example.demo.entities.TaskEntity;
import com.example.demo.entities.UserEntity;
import com.example.demo.exception.taskexceptions.TaskNotFound;

import com.example.demo.repositories.TaskRepository;
import com.example.demo.repositories.UserRepository;
import jakarta.transaction.Transactional;

import org.modelmapper.ModelMapper;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
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

    public List<TaskDto> getAllTasksByUserID(UUID userId) {
        List<TaskEntity> taskEntities = taskRepository.findByUser_Id(userId);
        return taskEntities.stream().map(task -> modelMapper.map(task, TaskDto.class)).toList();
    }

    public TaskDto getTask(UUID userId, UUID taskId)  {
        TaskEntity taskEntity = taskRepository.findByUser_IdAndTaskId(userId, taskId)
                .orElseThrow(()-> new TaskNotFound("Tarefa ou usuario nao encontrado"));

        return modelMapper.map(taskEntity, TaskDto.class);
    }

    @Transactional
    public void deleteTask( UUID userId, UUID taskId)  {
        if (!(taskRepository.existsByUser_IdAndId(userId, taskId))) {
            throw new TaskNotFound("Tarefa ou usuario nao encontrado");
        }
        taskRepository.deleteByUser_IdAndId(userId, taskId);
    }

    @Transactional
    public TaskDto updateTask(TaskUpdateDto taskDto, UUID userId, UUID taskId) {

        TaskEntity taskEntity = taskRepository.findByUser_IdAndTaskId(userId, taskId)
                        .orElseThrow(()-> new TaskNotFound("Tarefa ou usuario nao encontrado"));
        taskEntity.setName(taskDto.getName());
        taskEntity.setDescription(taskDto.getDescription());
        TaskEntity savedTask = (taskRepository.save(taskEntity));
        return modelMapper.map(savedTask, TaskDto.class);
    }

    //problema na conversão do User
    @Transactional
    public TaskDto createTask(TaskCreateDto taskDto, UserEntity user) {
            TaskEntity taskEntity = new TaskEntity();
            taskEntity.setName(taskDto.getName());
            taskEntity.setDescription(taskDto.getDescription());
            taskEntity.setUser(user);
            taskEntity.setDid(false);

            TaskEntity savedTask = taskRepository.save(taskEntity);

        return modelMapper.map(savedTask, TaskDto.class);
    }

    @Transactional
    public TaskDto handleTask(UUID userId, UUID taskId) {
        TaskEntity taskEntity = taskRepository.findByUser_IdAndTaskId(userId, taskId)
                        .orElseThrow(() -> new TaskNotFound("Tarefa ou usuario nao encontrado"));
        taskEntity.setDid(!taskEntity.isDid());
        TaskEntity savedTask = taskRepository.save(taskEntity);
        return modelMapper.map(savedTask, TaskDto.class);
    }
}

