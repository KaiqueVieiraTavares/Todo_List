package com.example.demo.services;

import com.example.demo.TestFixtures;
import com.example.demo.dtos.task.TaskCreateDto;
import com.example.demo.dtos.task.TaskDto;
import com.example.demo.dtos.task.TaskUpdateDto;
import com.example.demo.entities.TaskEntity;
import com.example.demo.entities.UserEntity;
import com.example.demo.exception.taskexceptions.TaskNotFound;

import com.example.demo.repositories.TaskRepository;
import com.example.demo.repositories.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

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
    private TaskUpdateDto taskUpdateDto;
    ArgumentCaptor<TaskEntity> taskEntityArgumentCaptor = ArgumentCaptor.forClass(TaskEntity.class);
    @BeforeEach
    void setup() {
        user = TestFixtures.createUserEntity(id, name, email, password);
        task = TestFixtures.createTaskEntity(id, name, description, did, user);
        taskDto = TestFixtures.createTaskDto(name, description, did, id, user.getId());
        taskCreateDto = TestFixtures.createTaskCreateDto(name, description);
        user.setTarefas(List.of(task));
        taskUpdateDto = TestFixtures.createTaskUpdateDto(name, description);
    }


    @Test
     void getTaskByUserIdAndId_whenUserIdAndTaskIdIsValidThenReturnTask(){
        when(taskRepository.findByUser_IdAndTaskId(user.getId(), task.getId())).thenReturn(Optional.of(task));
        when(modelMapper.map(task, TaskDto.class)).thenReturn(taskDto);

        TaskDto result = taskService.getTask(user.getId(), task.getId());

         assertThat(result).isNotNull().isEqualTo(taskDto);
         verify(taskRepository, times(1)).findByUser_IdAndTaskId(user.getId(), task.getId());
     }
     @Test
     void getTask_whenUserOrTaskDoesNotExist_thenThrowsException(){
        when(taskRepository.findByUser_IdAndTaskId(user.getId(), task.getId()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(()-> taskService.getTask(user.getId(), task.getId())).isInstanceOf(TaskNotFound.class);

        verify(taskRepository,times(1)).findByUser_IdAndTaskId(user.getId(), task.getId());
        verify(modelMapper,never()).map(any(), any());
     }
     @Test
    void getAllTasksByUserId_whenUserIdIsValid_thenReturnAllTasks(){
        List<TaskEntity> mockTasks = List.of(task, task);
        when(taskRepository.findByUser_Id(user.getId())).thenReturn(mockTasks);
        when(modelMapper.map(task, TaskDto.class)).thenReturn(taskDto);

        List<TaskDto> result = taskService.getAllTasksByUserID(user.getId());

         assertThat(result)
                 .isNotNull()
                 .hasSize(2)
                 .containsExactly(taskDto, taskDto);

        verify(taskRepository,times(1)).findByUser_Id(user.getId());
        verify(modelMapper, times(2)).map(task, TaskDto.class);
    }

    @Test
    void deleteTask_whenUserAndTaskIsValidThenDeleteTask(){
        when(taskRepository.existsByUser_IdAndId(user.getId(), task.getId())).thenReturn(true);
        taskService.deleteTask(user.getId(), task.getId());

        verify(taskRepository, times(1)).deleteByUser_IdAndId(user.getId(),
                task.getId());
    }
    @Test
    void deleteTask_whenUserOrTaskIsNotFoundThenThrownAnTaskNotFoundException(){
        when(taskRepository.existsByUser_IdAndId(user.getId(), task.getId())).thenReturn(false);
        assertThatThrownBy(()-> taskService.deleteTask(user.getId(), task.getId())).isInstanceOf(TaskNotFound.class);
        verify(taskRepository, times(1)).existsByUser_IdAndId(user.getId(), task.getId());
        verify(modelMapper, never()).map(any(), any());
    }
    @Test
    void updateTask_whenUserIdAndIdIsValidThenReturnTaskDto(){

        when(taskRepository.findByUser_IdAndTaskId(user.getId(), task.getId()))
                .thenReturn(Optional.of(task));

        // Configura o save para retornar a task modificada
        when(taskRepository.save(any(TaskEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(modelMapper.map(task, TaskDto.class)).thenReturn(taskDto);


        TaskDto result = taskService.updateTask(taskUpdateDto, task.getId(), user.getId());

        assertThat(result)
                .isNotNull()
                .isEqualTo(taskDto);

        verify(taskRepository, times(1)).save(any(TaskEntity.class));
        verify(modelMapper, times(1)).map(task, TaskDto.class);
    }

    @Test
    void updateTask_whenUserOrTaskIsNotValidThenThrowsException() {
        when(taskRepository.findByUser_IdAndTaskId(user.getId(), task.getId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.updateTask(taskUpdateDto, user.getId(), task.getId()))
                .isInstanceOf(TaskNotFound.class);
        verify(modelMapper, never()).map(any(), any());
        verify(taskRepository, never()).save(any());
    }
    @Test
    void createTask(){
        when(taskRepository.save(any(TaskEntity.class))).thenAnswer(arg -> arg.getArgument(0));
        when(modelMapper.map(any(TaskEntity.class), eq(TaskDto.class))).thenReturn(taskDto);

        TaskDto result = taskService.createTask(taskCreateDto, user);

        assertThat(result).isNotNull().isEqualTo(taskDto);

        verify(taskRepository,times(1)).save(taskEntityArgumentCaptor.capture());


        TaskEntity savedTask = taskEntityArgumentCaptor.getValue();

        assertThat(savedTask.getName()).isEqualTo(taskCreateDto.getName());
        assertThat(savedTask.getDescription()).isEqualTo(taskCreateDto.getDescription());
       assertThat(savedTask.getUser()).isEqualTo(user);
       assertThat(savedTask.isDid()).isFalse();

       verify(modelMapper,times(1)).map(savedTask, TaskDto.class);
    }
    @Test
    void handleTask_whenUserAndTaskIsValidThenHandleTask(){
        when(taskRepository.findByUser_IdAndTaskId(user.getId(), task.getId()))
                .thenReturn(Optional.of(task));
        when(modelMapper.map(task, TaskDto.class)).thenReturn(taskDto);
        when(taskRepository.save(taskEntityArgumentCaptor.capture())).thenAnswer( arg -> arg.getArgument(0));

        TaskDto result = taskService.handleTask(user.getId(), task.getId());
        TaskEntity savedTask = taskEntityArgumentCaptor.getValue();

        assertThat(savedTask.isDid()).isTrue();
        assertThat(result).isNotNull();

        verify(taskRepository,times(1)).findByUser_IdAndTaskId(user.getId(), task.getId());
        verify(modelMapper,times(1)).map(task, TaskDto.class);
    }

    @Test
    void handleTask_whenUserOrTaskIsNotValidThenThrowsException(){
        when(taskRepository.findByUser_IdAndTaskId(user.getId(), task.getId())).thenReturn(Optional.empty());


        assertThatThrownBy(() ->taskService.handleTask(user.getId(), task.getId()))
                .isInstanceOf(TaskNotFound.class);

        verify(modelMapper, never()).map(any(), any());
        verify(taskRepository, never()).save(any());
    }
}