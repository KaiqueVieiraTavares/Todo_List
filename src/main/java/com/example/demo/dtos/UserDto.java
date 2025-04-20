package com.example.demo.dtos;

import com.example.demo.enums.Role;

import java.util.List;
import java.util.UUID;

public record UserDto(UUID id, String name, String email, String password, List<Role> roles) {
}
