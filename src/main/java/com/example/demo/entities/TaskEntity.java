package com.example.demo.entities;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
public class TaskEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(unique = true, nullable = false)
    private String name;
    @Column(nullable = false)
    private String description;
    private boolean did;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;
}
