
package com.example.demo.integration.Task;

import com.example.demo.TestFixtures;
import com.example.demo.entities.TaskEntity;
import com.example.demo.entities.UserEntity;
import com.example.demo.repositories.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@DataJpaTest
public class TaskRepositoryIntegrationTest {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    private String name;
    private String email;
    private String password;
    private String description;
    private boolean did;

    private UserEntity user;
    private TaskEntity taskEntity;

    @BeforeEach
    void setup() {
        // Dados de teste
        name = "tarefa";
        email = "reginaldo@gmail.com";
        password = "123";
        description = "terminar tarefa";
        did = false;

        // 1) Cria UserEntity SEM ID e persiste
        user = TestFixtures.createUserEntity(null, name, email, password);
        testEntityManager.persistAndFlush(user);

        // 2) Cria TaskEntity SEM ID, vinculada ao user, e persiste
        taskEntity = TestFixtures.createTaskEntity(null, name, description, did, user);
        testEntityManager.persistAndFlush(taskEntity);
    }

    @Test
    void shouldReturnAnExceptionWhenDataIsDuplicated() {
        // Recupera o usuário já gerenciado
        UserEntity usersaved = testEntityManager.find(UserEntity.class, user.getId());

        // Cria nova TaskEntity duplicada para o mesmo usuário
        TaskEntity duplicatedTask = TestFixtures.createTaskEntity(
                null,
                name,
                description,
                did,
                usersaved
        );

        // Usa saveAndFlush para forçar o INSERT e o flush imediato
        assertThatThrownBy(() -> taskRepository.saveAndFlush(duplicatedTask))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void shouldFindTaskByUserIdAndTaskId() {
        Optional<TaskEntity> result = taskRepository.findByUser_IdAndTaskId(user.getId(), taskEntity.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo(taskEntity.getName());
    }

    @Test
    void shouldFindAllTasksByUserId() {
        List<TaskEntity> result = taskRepository.findByUser_Id(user.getId());

        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo(taskEntity.getName());
        assertThat(result.get(0).getDescription()).isEqualTo(taskEntity.getDescription());
    }

    @Test
    void shouldReturnTrueWhenUserIdAndTaskIdIsValid() {

        boolean result = taskRepository.existsByUser_IdAndId(user.getId(), taskEntity.getId());


        assertThat(result).isTrue();
    }

    @Test
    void shouldReturnFalseWhenUserIdAndTaskIdIsNotValid() {
        UUID nonExistentId = UUID.randomUUID();
        boolean result = taskRepository.existsByUser_IdAndId(nonExistentId, nonExistentId);

        assertThat(result).isFalse();
    }

    @Test
    void shouldDeleteAnTaskByUserIdAndTaskId() {
        UserEntity newUser = TestFixtures.createUserEntity(null, name, "kaique123@gmail.com", password);
        testEntityManager.persistAndFlush(newUser);
        TaskEntity newTask = TestFixtures.createTaskEntity(null, name, description, did, newUser);
        testEntityManager.persistAndFlush(newTask);
        taskRepository.deleteByUser_IdAndId(newUser.getId(), newTask.getId());
        testEntityManager.flush();
        testEntityManager.clear();
        Optional<TaskEntity> result = taskRepository.findById(newTask.getId());


        assertThat(result).isNotPresent();
    }

    @Test
    void shouldThrowAnExceptionWhenNameIsNull() {
        //testando com o name null
        TaskEntity test = TestFixtures.createTaskEntity(null, null, description, did, user);

        assertThatThrownBy(() -> taskRepository.saveAndFlush(test)).isInstanceOf(DataIntegrityViolationException.class);
    }
    @Test
    void shouldThrowAnExceptionWhenDescriptionIsNull(){
        //testando com a description null
        TaskEntity test = TestFixtures.createTaskEntity(null, name, null, did, user);

        assertThatThrownBy(()-> taskRepository.saveAndFlush(test)).isInstanceOf(DataIntegrityViolationException.class);
    }
}
