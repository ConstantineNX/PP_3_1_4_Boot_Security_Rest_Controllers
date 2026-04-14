package ru.kata.spring.boot_security.demo.service;

import ru.kata.spring.boot_security.demo.entity.Role;
import ru.kata.spring.boot_security.demo.entity.User;
import java.util.List;

public interface AdminUserService {
    List<User> findAllUsers();
    User saveUser(User user);
    void deleteUser(Long id);
    User updateUser(Long id, User user);
    User findUserById(Long id);
    User findUserByEmail(String email);
    User registerUser(User user);
    List<Role> findAllRoles();
    List<User> findAllWithRoles();
}
