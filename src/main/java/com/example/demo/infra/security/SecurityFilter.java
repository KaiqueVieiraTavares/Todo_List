package com.example.demo.infra.security;


import com.example.demo.entities.UserEntity;
import com.example.demo.repositories.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Component
public class SecurityFilter extends OncePerRequestFilter {
    private final TokenService tokenService;
    private final UserRepository userRepository;
    public SecurityFilter(TokenService tokenService, UserRepository userRepository) {
        this.tokenService = tokenService;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        var auth = this.recoverToken(request);
        if(auth!=null){
            var token = tokenService.verifyToken(auth);
            if(token!=null){
                Optional<UserEntity> user = userRepository.findByEmail(token);
                if(user.isPresent()){
                    CustomUserDetails customUserDetails = new CustomUserDetails(user.get());
                    var authorities = List.of(new SimpleGrantedAuthority("ROLE_" + user.get().getRole().name()));
                    var authentication = new UsernamePasswordAuthenticationToken(customUserDetails, null, authorities);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        }
        filterChain.doFilter(request, response);
    }

    private String recoverToken(HttpServletRequest request){
        String token = request.getHeader("Authorization");
        if(token==null) return null;
        return token.replace("Bearer ","");
    }

}
