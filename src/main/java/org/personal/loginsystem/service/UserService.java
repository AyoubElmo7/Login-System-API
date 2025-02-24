package org.personal.loginsystem.service;

import org.personal.loginsystem.entities.ForgotPassword;
import org.personal.loginsystem.entities.User;
import org.personal.loginsystem.exceptions.*;
import org.personal.loginsystem.repositories.UserRepository;
import org.personal.loginsystem.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;
import java.util.regex.Pattern;

@Service
public class UserService implements UserDetailsService {

    private static final String PASSWORD_REGEX = "^(?=.{8,})(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=]).*$";

    private final long expirationTime = 5 * 60 * 60 * 1000; // 5 Hours

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ForgotPasswordService forgotPasswordService;

    @Autowired
    public UserService(
            final JwtUtil jwtUtil,
            final UserRepository userRepository,
            final PasswordEncoder passwordEncoder,
            final ForgotPasswordService forgotPasswordService
    ) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.forgotPasswordService = forgotPasswordService;
    }

    public String registerUser(User user){
        if(userRepository.findUserByUsername(user.getUsername()).isPresent()) {
            throw new UsernameExistsException(user.getUsername());
        }

        if(userRepository.findUserByEmail(user.getEmail()).isPresent()) {
            throw new EmailExistsException(user.getEmail());
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);

        return "Account created successfully";
    }

    public Map<String, String> authenticateUser(User user) {
        User storedUser = userRepository.findUserByUsername(user.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException(user.getUsername()));

        if(!passwordEncoder.matches(user.getPassword(), storedUser.getPassword())){
            throw new IncorrectPasswordException();
        }

        return Collections.singletonMap("token", jwtUtil.generateToken(user.getUsername(), expirationTime, true));
    }

    public User findUserByEmail(String email) {
        return userRepository.findUserByEmail(email)
                .orElseThrow(() -> new EmailDoesNotExistException(email));
    }

    public String forgotPassword(ForgotPassword forgotPassword) {
        User storedUser = findUserByEmail(forgotPassword.getEmail());

        forgotPasswordService.forgotPassword(forgotPassword, storedUser);

        return "Email to change password sent successfully";
    }

    public String resetPassword(String token, String password) {
        String email = jwtUtil.getUsernameOrEmailFromToken(token, false);

        User storedUser = userRepository.findUserByEmail(email).orElseThrow(
                () -> new EmailDoesNotExistException(email)
        );

        if(!isValidPassword(password)) {
            throw new IncorrectPasswordException();
        }

        storedUser.setPassword(passwordEncoder.encode(password));
        userRepository.save(storedUser);

        return "Password successfully updated";
    }

    public boolean isValidPassword(String password) {
        return Pattern.matches(PASSWORD_REGEX, password);
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        return userRepository.findUserByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));
    }
}
