package ru.kata.spring.boot_security.demo.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ru.kata.spring.boot_security.demo.entity.User;

@Controller
public class PageController {

    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("user", new User());
        return "register-form";
    }

    @GetMapping("/login")
    public String loginForm() {return "login-form";}

    @GetMapping("/admin")
    public String admin() {
        return "admin/adminRest";
    }

    @GetMapping("/user")
    public String user() {
        return "admin/adminRest";
    }


}
