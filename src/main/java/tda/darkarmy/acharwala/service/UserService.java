package tda.darkarmy.acharwala.service;

import jakarta.mail.MessagingException;
import tda.darkarmy.acharwala.dto.LoginRequest;
import tda.darkarmy.acharwala.dto.LoginResponse;
import tda.darkarmy.acharwala.dto.PasswordResetRequest;
import tda.darkarmy.acharwala.dto.UserDto;
import tda.darkarmy.acharwala.model.User;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface UserService {

    // Get a user by email
    Optional<User> getUserByEmail(String email);

    // Get a user by ID
    Optional<User> getUserById(Long id);

    // Update a user's details
    User updateUser(UserDto userDto, Long id);

    // Delete a user by ID
    String deleteUser();

    // Check if an email is already registered
    boolean isEmailRegistered(String email);

    // Get a user by reset token
    User getUserByResetToken(String token);

    User signup(UserDto userDto) throws MessagingException, IOException;

    String verifyOtp(String email, Long otp);

    String verifyOtpPassword(String email, Long otp);

    LoginResponse login(LoginRequest loginRequest);

    User changePassword(PasswordResetRequest request) throws MessagingException;

    String deleteById(Long id);

    List<User> findAll();

    User getLoggedInUser();
}
