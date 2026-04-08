package ru.kata.spring.boot_security.demo.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.entity.User;
import ru.kata.spring.boot_security.demo.service.AdminUserService;
import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final AdminUserService userService;

    public AdminController(AdminUserService userService) {
        this.userService = userService;
    }

    @ModelAttribute("users")
    public List<User> findAll() {
        return userService.findAllUsers();
    }

    @ModelAttribute("user")
    public User newUser() {
        return new User();
    }

    @GetMapping()
    public String findAllUsers() {
        return "/admin/adminUsers";
    }

    @PostMapping("/save")
    public String saveUser(@ModelAttribute("user") User user) {
        userService.saveUser(user);
        return "redirect:/admin";
    }

    @PostMapping("/delete/{id}")
    public String deleteUser(@PathVariable("id") Long id) {
        userService.deleteUser(id);
        return "redirect:/admin";
    }

    @GetMapping("/update/{id}")
    public String updateUser(@PathVariable("id") Long id,
                             @RequestParam(required = false) String error,
                             Model model) {
        User user = userService.findUserById(id);
        model.addAttribute("user", user);
        model.addAttribute("actionUrl", "/admin/update/" + id);
        model.addAttribute("cancelUrl", "/admin");
        if (error != null) {
            model.addAttribute("error", error);
        }
        return "admin/update-form";
    }

    @PostMapping("/update/{id}")
    public String updateUser(@PathVariable("id") Long id,
                             @Valid @ModelAttribute("user") User user,
                             BindingResult bindingResult,
                             Model model) {
        if (bindingResult.hasErrors()) {
            return "admin/update-form";
        }
        try {
            userService.updateUser(id, user);
            return "redirect:/admin";
        } catch (EntityNotFoundException e) {
            model.addAttribute("error", e.getMessage());
            return "admin/update-form";
        }
    }

    @GetMapping("/search")
    public String search(@RequestParam Long id, Model model) {
        try {
            User user = userService.findUserById(id);
            model.addAttribute("foundUser", user);
        } catch (EntityNotFoundException e) {
            model.addAttribute("error", "the user was not found with the id " + id);
        }
        return "/admin/adminUsers";
    }
}
