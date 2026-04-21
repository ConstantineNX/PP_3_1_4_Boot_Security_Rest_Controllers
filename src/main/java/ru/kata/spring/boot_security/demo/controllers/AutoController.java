package ru.kata.spring.boot_security.demo.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.kata.spring.boot_security.demo.dto.UserRequestDto;
import ru.kata.spring.boot_security.demo.dto.UserResponseDto;
import ru.kata.spring.boot_security.demo.service.AdminUserService;
import javax.persistence.EntityExistsException;
import javax.validation.Valid;
import java.util.Map;

@RestController
public class AutoController {
    private final AdminUserService adminUserService;
    private final AuthenticationManager authenticationManager;

    public AutoController(AdminUserService adminUserService, AuthenticationManager authenticationManager) {
        this.adminUserService = adminUserService;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/api/register")
    public ResponseEntity<?> register(@Valid @RequestBody UserRequestDto user) {
       try {
           String rawPassword = user.getPassword();
           UserResponseDto user1 = adminUserService.registerUser(user);
           UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(user1.getEmail(), rawPassword);
           Authentication authResult = authenticationManager.authenticate(auth);
           SecurityContextHolder.getContext().setAuthentication(authResult);
           return ResponseEntity.status(HttpStatus.CREATED).body(user1);
       } catch (EntityExistsException e) {
           return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
       }
    }

    @PostMapping("/api/login")
    public ResponseEntity<?> login(@RequestBody UserRequestDto user) {
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(auth);
            return ResponseEntity.ok().body(user);
        } catch (BadCredentialsException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
