package com.example.demo.repositories;

import com.example.demo.entities.TaskEntity;
import com.example.demo.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TaskRepository extends JpaRepository<TaskEntity, UUID> {
    List<TaskEntity> findByUser_Id(UUID userId);



        @Query("SELECT t FROM TaskEntity t JOIN FETCH t.user u WHERE u.id = :userId AND t.id = :taskId")
        Optional<TaskEntity> findByUser_IdAndTaskId(
                @Param("userId") UUID userId,
                @Param("taskId") UUID taskId);

    boolean existsByUser_IdAndId(UUID userId, UUID taskId);
    void deleteByUser_IdAndId(UUID userId, UUID taskId);

    UUID user(UserEntity user);
}