package com.example.demo.services;

import com.example.demo.TestFixtures;
import com.example.demo.dtos.user.UserDto;
import com.example.demo.entities.TaskEntity;
import com.example.demo.entities.UserEntity;
import com.example.demo.enums.Role;
import com.example.demo.exception.userexceptions.UserNotFound;
import com.example.demo.repositories.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    private static final UUID id = UUID.randomUUID();
    private static final String name = "reginaldo";
    private static final String email = "reginaldo@gmail.com";
    private static final String password = "123";
    private static final List<TaskEntity> tasks = new ArrayList<>();
    @Mock
    private UserRepository userRepository;
    @Mock
    private ModelMapper modelMapper;
    @InjectMocks
    private UserService userService;
    private UserEntity user;
    private UserDto userDto;
    @BeforeEach
    void setup() {
        tasks.clear();
         user = TestFixtures.createUserEntity(id,name,email,password);
         userDto = TestFixtures.createUserDto(name,email,password);
    }
    @Test
    void getUser_whenUserThenReturnUser() {
        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        when(modelMapper.map(user,UserDto.class)).thenReturn(userDto);

        UserDto userDto1 = userService.getUser(id);

        assertThat(userDto1).isNotNull().isEqualTo(userDto);

        verify(userRepository,times(1)).findById(id);
        verify(modelMapper,times(1)).map(user, UserDto.class);
    }

    @Test
    void getUser_whenUserIsNotFoundThenThrownUserIsNotFoundException() {
        UUID idNotExistent = UUID.randomUUID();
        when(userRepository.findById(idNotExistent)).thenReturn(Optional.empty());

        assertThatThrownBy(()-> userService.getUser(idNotExistent))
                .isInstanceOf(UserNotFound.class);


        verify(userRepository,times(1)).findById(idNotExistent);
    }

    @Test
    void getAll_returnAllUsers(){
        when(userRepository.findAll()).thenReturn(List.of(user));
        when(modelMapper.map(user, UserDto.class)).thenReturn(userDto);

        List<UserDto> users= userService.getAll();

        assertThat(users).isNotNull();
        verify(userRepository,times(1)).findAll();
        verify(modelMapper,times(1)).map(user, UserDto.class);
    }

    @Test
    void deleteUser_whenIdIsValidThenDeleteUser(){
        when(userRepository.existsById(id)).thenReturn(true);

        userService.deleteUser(id);
       verify(userRepository,times(1)).deleteById(id);
    }
    @Test
    void deleteUser_whenIdIsNotValidThenThrownAnUserNotFoundException() {
        UUID invalidID = UUID.randomUUID();
        when(userRepository.existsById(invalidID)).thenReturn(false);

        assertThrows(UserNotFound.class, ()-> userService.deleteUser(invalidID));
    }

    @Test
    void updateUser_whenUserDtoAndIdIsValidThenUpdateUser() {
        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        doNothing().when(modelMapper).map(userDto,user);
        when(modelMapper.map(user, UserDto.class)).thenReturn(userDto);

        UserDto userDto1 = userService.updateUser(userDto, id);


        assertThat(userDto1).isNotNull().isEqualTo(userDto);
        verify(userRepository,times(1)).findById(id);
        verify(userRepository,times(1)).save(user);
        verify(modelMapper,times(1)).map(userDto,user);
        verify(modelMapper,times(1)).map(user, UserDto.class);
    }

    @Test
    void updateUser_whenUserIsNotFoundThenThrowAnUserIsNotFoundException(){
        UUID invalidId = UUID.randomUUID();
        when(userRepository.findById(invalidId)).thenReturn(Optional.empty());

        assertThrows(UserNotFound.class, () -> userService.updateUser(userDto, invalidId));
        verify(userRepository,never()).save(any());
        verify(modelMapper,never()).map(any(),any());
    }
}