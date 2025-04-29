package com.example.demo.dtos.auth;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRegisterDto {
    @NotEmpty @Size(min = 8, max = 20) private  String name;
    @NotEmpty @Size(min = 5) private String email;
    @NotEmpty @Size(min = 8) private String password;
}
