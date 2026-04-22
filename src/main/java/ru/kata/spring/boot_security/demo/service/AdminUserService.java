package ru.kata.spring.boot_security.demo.service;

import ru.kata.spring.boot_security.demo.dto.UserRequestDto;
import ru.kata.spring.boot_security.demo.dto.UserResponseDto;
import java.util.List;

public interface AdminUserService {
    List<UserResponseDto> findAllUsers();
    UserResponseDto saveUser(UserRequestDto user);
    void deleteUser(Long id);
    UserResponseDto updateUser(Long id, UserRequestDto user);
    UserResponseDto registerUser(UserRequestDto user);
    UserResponseDto findUserById(Long id);


}
