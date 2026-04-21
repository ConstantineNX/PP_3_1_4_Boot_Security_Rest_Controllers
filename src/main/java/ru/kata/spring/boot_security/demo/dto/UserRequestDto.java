package ru.kata.spring.boot_security.demo.dto;

import lombok.Getter;
import lombok.Setter;
import javax.validation.constraints.*;
import java.util.Set;

@Getter
@Setter
public class UserRequestDto {

    private Long id;

    @NotBlank(message = "First name is required")
    @Size(min = 3, max = 30, message = "First name must be between 3 and 30 characters")
    private String firstName;

    @Size(min = 3, max = 30, message = "Last name must be between 3 and 30 characters")
    private String lastName;

    @Min(value = 0, message = "возраст не может быть отрицательным или равен 0")
    @Max(value = 130, message = "возраст не может превышать 130 лет")
    private Integer age;

    private String city;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;

    @Size(min = 10, max = 12, message = "телефон должен быть в диапозоне 10-12 цифр")
    private String phone;

    @Size(min = 3, message = "пароль не может быть менее 3-символов")
    private String password;

    private Set<Long> roleIds;
}
