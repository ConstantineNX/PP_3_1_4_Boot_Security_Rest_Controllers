package ru.kata.spring.boot_security.demo.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.dto.UserRequestDto;
import ru.kata.spring.boot_security.demo.dto.UserResponseDto;
import ru.kata.spring.boot_security.demo.entity.Role;
import ru.kata.spring.boot_security.demo.entity.User;
import ru.kata.spring.boot_security.demo.repository.RoleRepository;
import ru.kata.spring.boot_security.demo.repository.UserRepository;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import java.util.*;
import java.util.stream.Collectors;

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

    User newUser(UserRequestDto user) {
        User user1 = new User();
        user1.setFirstName(user.getFirstName());
        user1.setLastName(user.getLastName());
        user1.setAge(user.getAge());
        user1.setCity(user.getCity());
        user1.setEmail(user.getEmail());
        user1.setPhone(user.getPhone());
        return user1;
    }

    @Transactional(readOnly = true)
    @Override
    public List<UserResponseDto> findAllUsers() {
        return userRepository.findAllWithRoles().stream()
                .map(UserResponseDto :: new)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public UserResponseDto saveUser(UserRequestDto user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new EntityExistsException("The user already exists");
        }
        User user1 = newUser(user);
        user1.setPassword(passwordEncoder.encode(user.getPassword()));
        if (user.getRoleIds() != null) {
            Set<Role> roles = new HashSet<>(roleRepository.findAllById(user.getRoleIds()));
            user1.setRoles(roles);
        }
        User savedUser = userRepository.save(user1);
        return new UserResponseDto(savedUser);
    }

    @Transactional
    @Override
    public void deleteUser(Long id) {
        userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));
        userRepository.deleteById(id);
    }

    @Transactional
    @Override
    public UserResponseDto updateUser(Long id, UserRequestDto user) {
        Objects.requireNonNull(user);
        Objects.requireNonNull(id);
        User user1 = userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User with id: " + id));
        user1.setFirstName(user.getFirstName());
        user1.setLastName(user.getLastName());
        user1.setAge(user.getAge());
        user1.setCity(user.getCity());
        user1.setEmail(user.getEmail());
        user1.setPhone(user.getPhone());
        if (user.getRoleIds() != null && !user.getRoleIds().isEmpty()) {
            Set<Role> roles = new HashSet<>(roleRepository.findAllById(user.getRoleIds()));
            user1.setRoles(roles);
        }
        User savedUser = userRepository.save(user1);
        return new UserResponseDto(savedUser);
    }

    @Transactional(readOnly = true)
    @Override
    public UserResponseDto findUserById(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Invalid user id"));
        return new UserResponseDto(user);
    }

    @Transactional
    @Override
    public UserResponseDto registerUser(UserRequestDto user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new EntityExistsException("Пользователь с Email: " + user.getEmail() + " уже существует");
        }
        User user1 = newUser(user);
        user1.setPassword(passwordEncoder.encode(user.getPassword()));
        Set<Role> roles = new HashSet<>();
        if (user.getEmail().matches("(?i)^(admin|administrator)[0-9]*@.*")) {
            Role adminRole = roleRepository.findByName("ROLE_ADMIN")
                    .orElseThrow(()-> new EntityNotFoundException("Role not found: ROLE_ADMIN"));
            roles.add(adminRole);
        }
        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(()-> new EntityNotFoundException("Role not found: ROLE_USER"));
        roles.add(userRole);
        user1.setRoles(roles);
        User savedUser = userRepository.save(user1);
        return new UserResponseDto(savedUser);
    }
}
