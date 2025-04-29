package com.example.demo.repositories;

import com.example.demo.entities.TaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TaskRepository extends JpaRepository<TaskEntity, UUID> {
    List<TaskEntity> findByUserId(UUID userId);

    Optional<TaskEntity> findByUserIdAndId(UUID userId, UUID taskId);

    boolean existsByUserIdAndId(UUID userId, UUID taskId);

    @Modifying
    @Query("DELETE FROM TaskEntity t WHERE t.user.id = :userId AND t.id = :taskId")
    int deleteByUserIdAndId(@Param("userId") UUID userId, @Param("taskId") UUID taskId);
}