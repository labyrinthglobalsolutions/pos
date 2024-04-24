package admin_user.service;

import admin_user.dto.LoginRequest;
import admin_user.dto.SignupRequest;
import admin_user.model.User;
import admin_user.repositories.UserRepo;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserServiceImpl implements UserService{

    @Autowired
    UserRepo userRepo;
    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();


    @Autowired
    private JavaMailSender javaMailSender;


    @Value("${spring.mail.username}")
    private String fromEmail;



    @Override
    public User registerUser(SignupRequest signupRequest) {
        User user = new User();
        user.setName(signupRequest.getName());
        user.setEmail(signupRequest.getEmail());
        user.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
        user.setRoles(signupRequest.getRoles());
        userRepo.save(user);
        return user;
    }



    @Override
    public String generateOtp(User user) {
        try {
            int otp = (int) (Math.random() * 9000) + 1000;
            user.setOtp(otp);
            userRepo.save(user);
            javaMailSender.send(optEmailSend(user, otp));
            return "success";
        } catch (Exception e) {
            e.printStackTrace();
            return "error";
        }
    }

    private SimpleMailMessage optEmailSend(User user, int otp) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setFrom(fromEmail);
        msg.setTo(user.getEmail());

        msg.setSubject("Log in to your account");
        msg.setText("Please enter the following verification code to verify this login attempt." + "\n\n" + otp + "\n\n" + "Regards \n" + "Vikas Kumar");
        return msg;
    }

    @Override
    public User saveOtpDetails(LoginRequest loginRequest) {
        User users = userRepo.findByEmail(loginRequest.getUsername());
        if (users.getOtp() == loginRequest.getOtp()) {

            userRepo.save(users);
            return users;
        }
        return null;
    }



    @Override
    public String sendResetEmail(String email, HttpServletRequest request) {
        try {
            User user = userRepo.findByEmail(email);
            if (user == null) {
                throw new UsernameNotFoundException("USER NOT FOUND");
            }
            String token = UUID.randomUUID().toString();
            user.setEmail(email);
            user.setPasswordToken(token);
            userRepo.save(user);
            javaMailSender.send(constructEmail(getAppUrl(request), request.getLocale(), token, user));
            return "SUCCESS";
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    private SimpleMailMessage constructEmail(String appUrl, Locale locale, String token, User user) {
        String url = appUrl + "/forgot/changePwd?token=" + token;
        return constructEmail("Reset Password",   " \r\n" + url, user);
    }

    private String getAppUrl(HttpServletRequest request) {
        return "http://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
    }

    private SimpleMailMessage constructEmail(String subject, String body,
                                             User user) {
        SimpleMailMessage email = new SimpleMailMessage();
        email.setSubject(subject);
        email.setText(body);
        email.setTo(user.getEmail());
        email.setFrom(fromEmail);
        return email;
    }




    @Override
    public String updatePassword(LoginRequest loginRequest) {
        User user = userRepo.findByPasswordToken(loginRequest.getPasswordToken());
        if (user != null) {
            user.setPassword(passwordEncoder.encode(loginRequest.getPassword()));
            userRepo.save(user);
            return "SUCCESS";
        }
        return null;
    }

    @Override
    public List<User> getAllUsers() {

        return userRepo.findAll();
    }


    @Override
    public User getUserById(Long userId) {
        return userRepo.findById(userId).orElse(null);
    }

    @Override
    public void updateUser(User user) {
        userRepo.save(user);
    }








    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepo.findByEmail(username);
        if (user == null) {
            throw new UsernameNotFoundException("USER NOT FOUND");
        }
        UserDetails userDetails = org.springframework.security.core.userdetails.User.withUsername(user.getEmail()).password(user.getPassword()).authorities(user.getRoles()).build();
        return userDetails;
    }
















}



