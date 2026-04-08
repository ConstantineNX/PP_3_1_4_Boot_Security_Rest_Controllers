package ru.kata.spring.boot_security.demo.controllers;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import ru.kata.spring.boot_security.demo.entity.User;
import ru.kata.spring.boot_security.demo.service.AdminUserService;
import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;

@Controller
public class UserController {

    private final AdminUserService userService;

    public UserController(AdminUserService userService) {
        this.userService = userService;
    }

    @GetMapping("/user")
    public String profileUser(@AuthenticationPrincipal User user, Model model) {
        User user1 = userService.findUserByEmail(user.getEmail());
        model.addAttribute("user", user1);
        return "user/profile-view";
    }

    @GetMapping("/user/edit")
    public String editUser(@AuthenticationPrincipal User user, Model model) {
        User user1 = userService.findUserById(user.getId());
        model.addAttribute("user", user1);
        model.addAttribute("actionUrl", "/user/edit");
        model.addAttribute("cancelUrl", "/user");
        return "admin/update-form";
    }

    @PostMapping("/user/edit")
    public String updateUser(@Valid @ModelAttribute User updateUser,
                             BindingResult result,
                             @AuthenticationPrincipal User currentUser,
                             Model model) {
        if (result.hasErrors()) {
            model.addAttribute("user", updateUser);
            return "admin/update-form";
        }
        Long id = currentUser.getId();
        try {
            userService.updateUser(id, updateUser);
            return "redirect:/user";
        } catch (EntityNotFoundException e) {
            model.addAttribute("error", e.getMessage());
            return "admin/update-form";
        }
    }
}
