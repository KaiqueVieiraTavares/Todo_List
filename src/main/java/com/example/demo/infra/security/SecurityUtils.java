package com.example.demo.infra.security;

import com.example.demo.entities.UserEntity;
import com.example.demo.enums.Role;
import com.example.demo.exception.userexceptions.UserNotFound;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.UUID;

public class SecurityUtils {
    public static UserEntity getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()){
            throw new UserNotFound("Usuario nao encontrado");
        }
        Object principal = auth.getPrincipal();

        if(principal instanceof String || "anonymousUser".equals(principal.toString())){
            throw new RuntimeException("Usuario anonimo");
        }

        if(principal instanceof CustomUserDetails user){
            return user.getUserEntity();
        }

        throw new RuntimeException("Tipo de usuario invalido");
    }

    public static boolean isAdmin(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth.getAuthorities().stream().anyMatch(authorities
                -> authorities.getAuthority().equals(Role.ADMIN.name()));
    }
    public static UUID getId(){
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(principal instanceof CustomUserDetails customUserDetails){
            return customUserDetails.getUserEntity().getId();
        }

        throw new RuntimeException("User nao autenticado");
    }
}
