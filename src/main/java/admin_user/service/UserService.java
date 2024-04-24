package admin_user.service;


import admin_user.dto.LoginRequest;
import admin_user.dto.SignupRequest;
import admin_user.model.User;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;
import java.util.Optional;

public interface UserService extends UserDetailsService {

    public User registerUser(SignupRequest signupRequest);
    public String generateOtp(User user);

    public User saveOtpDetails(LoginRequest loginRequest);

    public String sendResetEmail(String email, HttpServletRequest request);

    public String updatePassword(LoginRequest loginRequest);
    User getUserById(Long userId);

    void updateUser(User user);




    List<User> getAllUsers();








}