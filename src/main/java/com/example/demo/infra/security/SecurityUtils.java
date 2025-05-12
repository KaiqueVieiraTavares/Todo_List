package com.example.demo.infra.security;

import com.example.demo.entities.UserEntity;
import com.example.demo.enums.Role;
import com.example.demo.exception.userexceptions.UserNotFound;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.UUID;


@Component
public class SecurityUtils {
    public boolean isAdmin(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth.getAuthorities().stream().anyMatch(authorities
                -> authorities.getAuthority().equals("ROLE_ADMIN"));
    }
    public UUID getId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("Usuário não autenticado");
        }

        // Verifica se é uma instância de CustomUserDetails
        if (authentication.getPrincipal() instanceof CustomUserDetails customUserDetails) {
            return customUserDetails.getUserEntity().getId();
        }

        // Se não for, tenta extrair o ID diretamente do nome (que pode ser o ID em string)
        try {
            return UUID.fromString(authentication.getName());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("ID do usuário inválido no token");
        }
    }
}
