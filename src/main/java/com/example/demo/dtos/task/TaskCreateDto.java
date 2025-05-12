package com.example.demo.dtos.task;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskCreateDto {
    @NotBlank(message = "O titulo não deve ser nulo")
    private String name;

    @NotBlank(message = "A descrição nao deve ser nula")
    private String description;
}
