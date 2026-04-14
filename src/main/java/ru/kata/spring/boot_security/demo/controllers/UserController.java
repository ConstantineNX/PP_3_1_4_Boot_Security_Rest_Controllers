package ru.kata.spring.boot_security.demo.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.entity.User;
import ru.kata.spring.boot_security.demo.service.AdminUserService;
import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import ru.kata.spring.boot_security.demo.entity.Role;

@Controller
public class UserController {

    private final AdminUserService userService;

    public UserController(AdminUserService userService) {
        this.userService = userService;
    }

    @GetMapping("/user")
    public String profileUser(@AuthenticationPrincipal User user,
                              Model model) {
        boolean isUser = false;
        for (Role role : user.getRoles()) {
            if (role.getName().equals("ROLE_USER")) {
                isUser = true;
                break;
            }
        }
        User user1 = userService.findUserByEmail(user.getEmail());
        model.addAttribute("currentUser", user);
        model.addAttribute("view", "USER" );
        model.addAttribute("user", user);
        model.addAttribute("users", user1);
        model.addAttribute("isUser", isUser);
        return "admin/adminBootstrap";
    }

    @PostMapping("/user/edit")
    @ResponseBody
    public ResponseEntity<?> updateUser(@Valid @ModelAttribute("user") User updateUser,
                                     BindingResult result,
                                     @AuthenticationPrincipal User currentUser) {
        if (result.hasErrors()) {
            HashMap<String, String> errors = new HashMap<>();
            result.getFieldErrors().stream()
                            .filter(error -> !error.getField().equals("password"))
                            .filter(errorPassword -> !errorPassword.getField().equals("roles"))
                            .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
            if (!errors.isEmpty()) {
                return ResponseEntity.badRequest().body(errors);
            }
        }
        Long id = currentUser.getId();
        try {
            userService.updateUser(id, updateUser);
            return ResponseEntity.ok().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }
}
