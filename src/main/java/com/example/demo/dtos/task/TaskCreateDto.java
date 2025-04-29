package com.example.demo.dtos.task;

import com.example.demo.entities.UserEntity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskCreateDto {
    @NotBlank(message = "O titulo não deve ser nulo") private String name;
    @NotBlank(message = "A descrição nao deve ser nula") private  String description;
}
