package com.example.demo.infra.security;


import com.example.demo.dtos.auth.ResponseDto;
import com.example.demo.dtos.auth.UserLoginDto;
import com.example.demo.dtos.auth.UserRegisterDto;
import com.example.demo.entities.UserEntity;
import com.example.demo.enums.Role;
import com.example.demo.exception.userexceptions.EmailAlreadyExistsException;
import com.example.demo.repositories.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final TokenService tokenService;
    public AuthService(PasswordEncoder passwordEncoder, UserRepository userRepository, TokenService tokenService, ModelMapper modelMapper) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.tokenService = tokenService;
    }

    public ResponseDto login(UserLoginDto userLoginDto) throws BadCredentialsException {
        UserEntity user = userRepository.findByEmail(userLoginDto.getEmail()).orElseThrow(()-> new UsernameNotFoundException("Usuario nao encontrado"));
        if(!passwordEncoder.matches(userLoginDto.getPassword(), user.getPassword())){
            throw new BadCredentialsException("Senha incorreta");
        }
        String token = tokenService.generateToken(user);
        return new ResponseDto(user.getId(), user.getName(), token);
    }

    public ResponseDto registerUser(UserRegisterDto userRegisterDto){
        if (userRegisterDto.getPassword() == null || userRegisterDto.getPassword().isEmpty()) {
            throw new IllegalArgumentException("A senha não pode ser nula ou vazia");
        }
        if(userRepository.existsByEmail(userRegisterDto.getEmail())){
            throw new EmailAlreadyExistsException("Email ja existe");
        }
        UserEntity user = new UserEntity();
        user.setName(userRegisterDto.getName());
        user.setEmail(userRegisterDto.getEmail());
        user.setPassword(passwordEncoder.encode(userRegisterDto.getPassword()));
        user.setRole(Role.USER);
        userRepository.save(user);
        String token = tokenService.generateToken(user);
        return new ResponseDto(user.getId(), user.getName(), token);
    }
}
