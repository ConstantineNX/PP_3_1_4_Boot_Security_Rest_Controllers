package ru.kata.spring.boot_security.demo.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.dto.UserRequestDto;
import ru.kata.spring.boot_security.demo.dto.UserResponseDto;
import ru.kata.spring.boot_security.demo.service.AdminUserService;
import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class RestAdminController {

    private final AdminUserService adminUserService;

    public RestAdminController(AdminUserService adminUserService) {
        this.adminUserService = adminUserService;
    }

    @GetMapping("/users")
    public List<UserResponseDto> getAllUsers() {
        return adminUserService.findAllUsers();
    }

    @PostMapping("/users")
    public ResponseEntity<?> saveUser(@Valid @RequestBody UserRequestDto user,
                                        BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            Map<String, String> error = new HashMap<>();
            bindingResult.getFieldErrors().forEach(e -> error.put(e.getField(), e.getDefaultMessage()));
            return ResponseEntity.badRequest().body(error);
        }
        try {
            UserResponseDto user1 = adminUserService.saveUser(user);
            return ResponseEntity.status(HttpStatus.CREATED).body(user1);

        } catch (EntityNotFoundException e) {
            String message = e.getMessage() == null ? "User not found" : e.getMessage();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", message));
        }
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id,
                                        @Valid @RequestBody UserRequestDto user,
                                        BindingResult result) {
        if (result.hasErrors()) {
            HashMap<String, String> errors = new HashMap<>();
            result.getFieldErrors().stream()
                    .filter(errorsPassword -> !errorsPassword.getField().equals("password"))
                    .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
            if (!errors.isEmpty()) {
                return ResponseEntity.badRequest().body(errors);
            }
        }
        try {
            UserResponseDto updateUser = adminUserService.updateUser(id, user);
            return ResponseEntity.ok().body(updateUser);
        } catch (EntityNotFoundException e) {
            String message = e.getMessage() != null ? e.getMessage() : "User not found";
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", message));
        }
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        adminUserService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
