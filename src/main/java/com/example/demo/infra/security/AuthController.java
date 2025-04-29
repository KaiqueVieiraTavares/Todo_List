package com.example.demo.infra.security;


import com.example.demo.dtos.auth.ResponseDto;
import com.example.demo.dtos.auth.UserLoginDto;
import com.example.demo.dtos.auth.UserRegisterDto;
import com.example.demo.repositories.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final UserRepository userRepository;
    private final TokenService tokenService;
    private final AuthService authService;

    public AuthController(UserRepository userRepository, TokenService tokenService, AuthService authService) {
        this.userRepository = userRepository;
        this.tokenService = tokenService;
        this.authService = authService;
    }
    @PostMapping("/register")
    public ResponseEntity<ResponseDto> cadasterUser(@RequestBody UserRegisterDto userRegisterDto){
        ResponseDto register = authService.registerUser(userRegisterDto);
        return ResponseEntity.ok(register);
    }
    @PostMapping("/login")
    public ResponseEntity<ResponseDto> loginUser(@RequestBody UserLoginDto userLoginDto){
        ResponseDto responseDto = authService.login(userLoginDto);
        return ResponseEntity.ok(responseDto);
    }
}
