package com.example.demo;

import com.example.demo.dtos.task.TaskCreateDto;
import com.example.demo.dtos.task.TaskDto;
import com.example.demo.dtos.user.UserDto;
import com.example.demo.entities.TaskEntity;
import com.example.demo.entities.UserEntity;
import com.example.demo.enums.Role;

import java.util.ArrayList;
import java.util.UUID;

public class TestFixtures {

    public static UserDto createUserDto(String name, String email, String password){
        return new UserDto(name,email,password);
    }
    public static UserEntity createUserEntity(UUID id, String name,String email,String password){
        return new UserEntity(id, name, email,password, Role.USER,new ArrayList<>());
    }

    public static TaskEntity createTaskEntity(UUID id, String name, String description,boolean did, UserEntity user){
        return new TaskEntity(id,name,description,did,user);
    }

    public static TaskCreateDto createTaskCreateDto(String name, String description){
        return new TaskCreateDto(name, description);
    }

    public static TaskDto createTaskDto(String name, String description, boolean did, UUID userId){
        return new TaskDto(name, description, did, userId);
    }
}
