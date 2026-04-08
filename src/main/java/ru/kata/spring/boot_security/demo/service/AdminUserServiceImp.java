package ru.kata.spring.boot_security.demo.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.entity.Role;
import ru.kata.spring.boot_security.demo.entity.User;
import ru.kata.spring.boot_security.demo.repository.RoleRepository;
import ru.kata.spring.boot_security.demo.repository.UserRepository;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Service
public class AdminUserServiceImp implements AdminUserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;


    public AdminUserServiceImp(UserRepository userRepository, PasswordEncoder passwordEncoder, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
    }

    @Transactional(readOnly = true)
    @Override
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    @Transactional
    @Override
    public User saveUser(User user) {
        Objects.requireNonNull(user);
        Objects.requireNonNull(user.getFirstName());
        Objects.requireNonNull(user.getEmail());
        if (user.getFirstName().trim().isEmpty() || user.getEmail().trim().isEmpty()) {
            throw new EntityNotFoundException("The user must have at least a name and email adress");
        }
        if (user.getFirstName().length() < 3 || user.getFirstName().length() > 30) {
            throw new EntityNotFoundException("The first name must be between 3 and 30 characters");
        }
        return userRepository.save(user);
    }

    @Transactional
    @Override
    public void deleteUser(Long id) {
        userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));
        userRepository.deleteById(id);
    }

    @Transactional
    @Override
    public User updateUser(Long id, User user) {
        Objects.requireNonNull(user);
        Objects.requireNonNull(id);
        User user1 = userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User with id: " + id));
        user1.setFirstName(user.getFirstName());
        user1.setLastName(user.getLastName());
        user1.setAge(user.getAge());
        user1.setCity(user.getCity());
        user1.setEmail(user.getEmail());
        user1.setPhone(user.getPhone());
        return userRepository.save(user1);
    }

    @Transactional(readOnly = true)
    @Override
    public User findUserById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Invalid user id"));
    }

    @Override
    public User findUserByEmail(String email) {
        return userRepository.findByEmailWithRoles(email).orElseThrow(() -> new EntityNotFoundException("Invalid user email"));
    }

    @Transactional
    @Override
    public User registerUser(User user) {
        try {
            findUserByEmail(user.getEmail());
                throw new EntityExistsException("Пользователь с Email: " + user.getEmail() + " уже существует");
        } catch (EntityNotFoundException e) {
//        email свободен, можно регистрировать
        }
        String warPassword = user.getPassword();
        user.setPassword(passwordEncoder.encode(warPassword));
        String roleName = user.getEmail().matches("(?i)^(admin|administrator)[0-9]*@.*") ? "ROLE_ADMIN" : "ROLE_USER";
        Role role = roleRepository
                .findByName(roleName)
                .orElseThrow(() -> new EntityNotFoundException("Role not found with name: " + roleName));
        user.setRoles(Collections.singleton(role));
        return userRepository.save(user);
    }
}
