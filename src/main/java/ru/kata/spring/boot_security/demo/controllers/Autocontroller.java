package ru.kata.spring.boot_security.demo.controllers;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import ru.kata.spring.boot_security.demo.entity.Role;
import ru.kata.spring.boot_security.demo.entity.User;
import ru.kata.spring.boot_security.demo.repository.RoleRepository;
import ru.kata.spring.boot_security.demo.service.AdminUserService;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import java.util.Collections;

@Controller
public class Autocontroller {
    private final AdminUserService adminUserService;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final AuthenticationManager authenticationManager;

    public Autocontroller(AdminUserService adminUserService, PasswordEncoder passwordEncoder, RoleRepository roleRepository, AuthenticationManager authenticationManager) {
        this.adminUserService = adminUserService;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
        this.authenticationManager = authenticationManager;
    }

    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("user", new User());
        return "register-form";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute User user, Model model) {
       try {
           User user1 = adminUserService.registerUser(user);
           UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(user1.getEmail(), user1.getPassword());
           Authentication authResult = authenticationManager.authenticate(auth);
           SecurityContextHolder.getContext().setAuthentication(authResult);
           if (user1.getRoles().stream().anyMatch(r -> r.getName().equals("ROLE_ADMIN"))) {
               return "redirect:/admin";
           } else {
               return "redirect:/user";
           }
       } catch (EntityExistsException e) {
           model.addAttribute("error", e.getMessage());
           return "register-form";
       }
    }

    @GetMapping("/login")
    public String loginForm() {
        return "login-form";
    }
}
