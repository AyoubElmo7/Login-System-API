package org.personal.loginsystem.controllers;

import jakarta.validation.Valid;
import jakarta.validation.groups.Default;
import org.personal.loginsystem.entities.ForgotPasswordDTO;
import org.personal.loginsystem.entities.UserDTO;
import org.personal.loginsystem.service.UserService;
import org.personal.loginsystem.validators.OnCreate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(final UserService userService) {
        this.userService = userService;
    }

    @PostMapping({"/login"})
    public Map<String, String> login(
            @RequestBody @Valid UserDTO userDTO
    ) {
        return userService.authenticateUser(userDTO.toUser());
    }

    @PutMapping({"/register"})
    public String register(
            @Validated({OnCreate.class, Default.class}) @RequestBody UserDTO user
    ) {
        return userService.registerUser(user.toUser());
    }

    @PostMapping({"/forgotPassword"})
    public String forgotPassword(
            @RequestBody @Valid ForgotPasswordDTO forgotPasswordDTO
            ) {
        return userService.forgotPassword(forgotPasswordDTO.toForgotPasswordRequest());
    }

    @PostMapping({"/resetPassword"})
    public String resetPassword(
            @RequestBody String password,
            @RequestParam String token
    ) {
        return userService.resetPassword(token, password);
    }
}
