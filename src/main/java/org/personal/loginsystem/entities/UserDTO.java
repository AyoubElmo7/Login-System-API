package org.personal.loginsystem.entities;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.personal.loginsystem.enums.Role;
import org.personal.loginsystem.validators.OnCreate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    @NotBlank
    @Size(min = 6, message = "Username must be at least 6 characters long")
    private String username;
    @NotBlank
    @Pattern(regexp = "^(?=.*[!@#$%^&*()_+{}:\"<>?])[A-Za-z\\d!@#$%^&*()_+{}:\"<>?]{8,}$",
            message = "Password much be at least 8 characters long, containing one special character, one upper case and one lower case letter.")
    private String password;
    @Email(groups = OnCreate.class)
    @NotBlank(groups = OnCreate.class)
    private String email;
    @NotBlank(groups = OnCreate.class)
    private String securityQuestion;
    @NotBlank(groups = OnCreate.class)
    private String securityAnswer;
    private Role role = Role.USER;

    public User toUser() {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setSecurityQuestion(securityQuestion);
        user.setSecurityAnswer(securityAnswer);
        user.setPassword(password);
        user.setRole(role);
        return user;
    }
}