package tda.darkarmy.acharwala.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tda.darkarmy.acharwala.dto.LoginRequest;
import tda.darkarmy.acharwala.dto.PasswordResetRequest;
import tda.darkarmy.acharwala.dto.UserDto;
import tda.darkarmy.acharwala.dto.VerifyOtpDto;
import tda.darkarmy.acharwala.service.UserService;

import java.io.IOException;

import static org.springframework.http.ResponseEntity.status;

@Slf4j
@RestController
@RequestMapping("/api/v1/users")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Tag(name = "User Controller", description = "APIs for auth and profile management")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/all")
    public ResponseEntity<?> getALl(){
        return status(200).body(userService.findAll());
    }

    @GetMapping("/logged-in-user")
    public ResponseEntity<?> getLoggedInUser(){
        return status(200).body(userService.getLoggedInUser());
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody UserDto userDto) throws MessagingException, IOException {
        return status(201).body(userService.signup(userDto));
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestBody VerifyOtpDto verifyOtpDto) {
        log.info("Verify Otp: {}", verifyOtpDto.toString());
        return status(201).body(userService.verifyOtp(verifyOtpDto.getEmail(), verifyOtpDto.getOtp()));
    }

    @PostMapping("/verify-otp-password")
    public ResponseEntity<?> verifyOtpPassword(@RequestBody VerifyOtpDto verifyOtpDto) {
        return status(201).body(userService.verifyOtpPassword(verifyOtpDto.getEmail(), verifyOtpDto.getOtp()));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest){
        return status(200).body(userService.login(loginRequest));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> update(@RequestBody UserDto userDto, @PathVariable("id") Long id){
        return status(200).body(userService.updateUser(userDto, id));
    }

    @PutMapping("/reset-password")
    public ResponseEntity<?> changePassword(@RequestBody PasswordResetRequest request) throws MessagingException {
        return status(200).body(userService.changePassword(request));
    }

    @DeleteMapping("/")
    public ResponseEntity<?> delete(){
        return status(200).body(userService.deleteUser());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUserById(@PathVariable("id") Long id){
        return status(200).body(userService.deleteById(id));
    }
}
