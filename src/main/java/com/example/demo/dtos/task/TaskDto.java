package com.example.demo.dtos.task;

import com.example.demo.dtos.user.UserDto;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskDto{
   @NotBlank(message = "o titulo é obrigatório") private String name;
   @NotBlank(message = "a descrição é obrigatoria")private String description;
   private boolean did;
   private UUID userId;
}
