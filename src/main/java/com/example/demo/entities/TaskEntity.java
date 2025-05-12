package com.example.demo.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "task_entity",
uniqueConstraints = @UniqueConstraint(columnNames = {"name", "user_id"}))
public class TaskEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String description;

    private boolean did;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;


}
