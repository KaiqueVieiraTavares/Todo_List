package com.example.demo.services;

import com.example.demo.dtos.UserDto;
import com.example.demo.entities.UserEntity;
import com.example.demo.exception.UserNotFound;
import com.example.demo.repositories.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    public UserService (UserRepository userRepository, ModelMapper modelMapper){
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
    }

    public UserDto getUser(UUID id){
        UserEntity user = userRepository.findById(id).orElseThrow(()-> new UserNotFound("usuario nao encontrado"));
        return modelMapper.map(user, UserDto.class);
    }
    public List<UserDto> getAll(){
        List<UserEntity> user = userRepository.findAll();
        return user.stream().map(users -> modelMapper.map(users, UserDto.class)).toList();
    }
    public void deleteUser(UUID id){
        userRepository.deleteById(id);
    }
    public UserDto updateUser(UserDto userDto, UUID id){
        UserEntity user = userRepository.findById(id).orElseThrow(()-> new UserNotFound("usuario nao encontrado"));
        modelMapper.map(userDto,user);
        userRepository.save(user);
        return modelMapper.map(user, UserDto.class);
    }
}
