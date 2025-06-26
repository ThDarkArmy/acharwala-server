package tda.darkarmy.acharwala.service.impl;

import jakarta.mail.MessagingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import tda.darkarmy.acharwala.config.JwtTokenProvider;
import tda.darkarmy.acharwala.dto.LoginRequest;
import tda.darkarmy.acharwala.dto.LoginResponse;
import tda.darkarmy.acharwala.dto.PasswordResetRequest;
import tda.darkarmy.acharwala.dto.UserDto;
import tda.darkarmy.acharwala.enums.Role;
import tda.darkarmy.acharwala.exception.ResourceNotFoundException;
import tda.darkarmy.acharwala.exception.UserAlreadyExistsException;
import tda.darkarmy.acharwala.model.User;
import tda.darkarmy.acharwala.repository.UserRepository;
import tda.darkarmy.acharwala.service.UserService;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import static tda.darkarmy.acharwala.mapper.UserMapper.toUserEntity;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private MailSenderService mailSenderService;


    public User signup(UserDto userDto) throws MessagingException, IOException {

        Optional<User> userOptional = userRepository.findByEmail(userDto.getEmail());
        if(userOptional.isPresent() && userOptional.get().isEmailVerified()) throw new UserAlreadyExistsException("User with given email already exists");

        userOptional.ifPresent(user -> userRepository.deleteById(user.getId()));
        User user = toUserEntity(userDto);
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user.setRole(Role.valueOf(userDto.getRole()));
        System.out.println("UserDto"+ userDto+" "+ user);
        Long otp = new Random().nextLong(9999 - 1000 + 1) + 1000;
        user.setOtp(otp);

        mailSenderService.sendOtpEmail(user.getEmail(), user.getName(), otp.toString());
        user = userRepository.save(user);

        return user;
    }

    public LoginResponse login(LoginRequest loginRequest){
        Optional<User> userOptional =  userRepository.findByEmail(loginRequest.getEmail());

        if(userOptional.isEmpty()) throw new ResourceNotFoundException("User with given email not found.");
        if(!userOptional.get().isEmailVerified()) throw new ResourceNotFoundException("User is not verified yet.");

        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = jwtTokenProvider.generateToken(authentication);

        return new LoginResponse(token, userOptional.get());
    }

    public String verifyOtp(String email, Long otp){
        User user =  userRepository.findByEmail(email).orElseThrow(()-> new ResourceNotFoundException("User not found"));
        if(user.getOtp().longValue()==otp.longValue()){
            user.setEmailVerified(true);
            userRepository.save(user);
            return "Otp verified successfully";
        }else{
            return "Invalid otp";
        }
    }


    @Override
    public Optional<User> getUserByEmail(String email) {
        return Optional.empty();
    }

    @Override
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public User updateUser(UserDto userDto, Long id){
        User user = userRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("User not found"));

        user = toUserEntity(userDto);
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        return userRepository.save(user);
    }

    public User changePassword(PasswordResetRequest resetRequest) throws MessagingException {

        Optional<User> userOptional = userRepository.findByEmail(resetRequest.getEmail());
        if(userOptional.isEmpty()){
            throw new ResourceNotFoundException("No user found with given email.");
        }
        User user = userOptional.get();
        Long otp = new Random().nextLong(9999 - 1000 + 1) + 1000;
        user.setOtp(otp);

        mailSenderService.send(user, otp.toString());
        user.setNewPassword(passwordEncoder.encode(resetRequest.getPassword()));
        user.setPassword(passwordEncoder.encode(resetRequest.getPassword()));
        return userRepository.save(user);
    }

    public String getLoggedInUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null) {
            Object principal = authentication.getPrincipal();

            if (principal instanceof UserDetails) {
                return ((UserDetails) principal).getUsername(); // Return username from UserDetails
            } else if (principal instanceof String) {
                return (String) principal; // Principal is a String
            }
        }
        throw new IllegalStateException("No authenticated user found");
    }

    public User getLoggedInUser(){
        return userRepository.findByEmail(getLoggedInUsername()).get();
    }

    public User getById(Long id){
        return userRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("User not found"));
    }

    public Optional<User> getUser(Long userId){
        return userRepository.findById(userId);
    }

    public String deleteUser(){
        User user = getLoggedInUser();
        userRepository.deleteById(user.getId());

        return "User deleted successfully";
    }

    @Override
    public boolean isEmailRegistered(String email) {
        return false;
    }

    @Override
    public User getUserByResetToken(String token) {
        return null;
    }

    public String deleteById(Long id) {
        if(getUser(id).isEmpty()) throw new ResourceNotFoundException("User not found");
        userRepository.deleteById(id);
        return "User deleted successfully";
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public String deleteAllUsers() {
        userRepository.deleteAll();
        return "All users deleted successfully";
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public String verifyOtpPassword(String email, Long otp) {
        Optional<User> userOptional =  userRepository.findByEmail(email);
        User user = userOptional.get();
        if(user.getOtp().longValue()==otp.longValue()){
            user.setPassword(user.getNewPassword());
            userRepository.save(user);
            return "Otp verified successfully";
        }else{
            return "Invalid otp";
        }
    }
}
