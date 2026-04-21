package ru.kata.spring.boot_security.demo.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.kata.spring.boot_security.demo.repository.UserRepository;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    private final UserRepository userRepository;

    public WebSecurityConfig(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
           http
                   .csrf().disable()
                   .authorizeRequests()
                   .antMatchers("/","/login", "/register", "/css/**").permitAll()
                   .antMatchers("/api/register","/api/login" ).permitAll()
                   .antMatchers("/api/admin/**").hasRole("ADMIN")
                   .antMatchers("/api/user/**").hasAnyRole("USER", "ADMIN")
                   .anyRequest().authenticated()
                   .and()
                   .formLogin().loginPage("/login").usernameParameter("email")
                   .permitAll()
                   .and()
                   .logout()
                   .permitAll();
    }

    @Bean
    @Override
    public UserDetailsService userDetailsService() {
             return username -> userRepository
                      .findByEmailWithRoles(username)
                      .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authentication) throws Exception {
        return authentication.getAuthenticationManager();
    }
}