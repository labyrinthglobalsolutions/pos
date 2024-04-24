package admin_user.controller;


import admin_user.dto.JwtUtil;
import admin_user.dto.LoginRequest;
import admin_user.model.User;
import admin_user.repositories.UserRepo;
import admin_user.service.UserService;
import admin_user.service.UserServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.authentication.AuthenticationManager;

import org.springframework.security.core.Authentication;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.*;

import java.security.Principal;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Controller

public class LoginController {
    @Autowired
    UserService userService;

    @Autowired
    UserServiceImpl userServiceImpl;


    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepo userRepo;




    @Autowired
    JwtUtil jwtUtil;

    @GetMapping("/login")
    public String login() {

        return "pages/login";
    }

    @PostMapping ("/login")
    public void loginUser(@ModelAttribute LoginRequest loginRequest) {
        userService.loadUserByUsername(loginRequest.getUsername());
    }

    @GetMapping("/getOtp")
    public String getOtp(@ModelAttribute LoginRequest loginRequest, Model model) {
        model.addAttribute("otp", loginRequest.getOtp());
        return "pages/otpscreen";
    }

    @PostMapping("/sendOtp")
    public String saveOtpRecords(@ModelAttribute LoginRequest loginRequest, Authentication authentication, Model model, HttpServletRequest request) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        loginRequest.setUsername(userDetails.getUsername());
        User users = userService.saveOtpDetails(loginRequest);

        if (users != null) {
            // Check if the user is enabled
            if (!users.isStatus() && !"ADMIN".equals(authentication.getAuthorities().stream().findFirst().orElse(null).getAuthority())) {
                return "redirect:/login?disabled"; // Redirect with a disabled message
            }

            users.setStatus(true);
            userRepo.save(users);

            // Generate JWT token
            String jwtToken = jwtUtil.generateToken(String.valueOf(userDetails));

            // Save user role in the session
            String role = authentication.getAuthorities().stream().findFirst().orElse(null).getAuthority();
            HttpSession session = request.getSession();
            session.setAttribute("USER_ROLE", role);

            // Save JWT token with role-specific attribute names
            session.setAttribute("USERNAME_" + role, userDetails.getUsername());
            session.setAttribute("JWT_TOKEN_" + role, jwtToken);

            // Print JWT token to console for debugging
            System.out.println("JWT Token: " + jwtToken);

            // Print session attributes to console for debugging
            Enumeration<String> attributeNames = session.getAttributeNames();
            while (attributeNames.hasMoreElements()) {
                String attributeName = attributeNames.nextElement();
                System.out.println("Session Attribute: " + attributeName + " - " + session.getAttribute(attributeName));
            }

            model.addAttribute("userDetails", users.getEmail());
            model.addAttribute("username", userDetails.getUsername());
            model.addAttribute("role", role);

            // Check user roles and redirect accordingly
            if ("ADMIN".equals(role)) {
                return"redirect:/pages/dashboard";
            } else if ("INVENTORY_STAFF".equals(role)) {
                return "redirect:/staffdashboard";
            } else {
                // Default redirection if no specific role is found
                return "redirect:/defaultPage";
            }
        } else {
            return "redirect:getOtp?error";
        }
    }





    @GetMapping("/logout")
    public String logout(String username, HttpSession session) {
        User user = userRepo.findByEmail(username);

        if (user != null) {
            // Update user status to inactive
            user.setStatus(false);
            userRepo.save(user);

            // Remove JWT token and other session attributes
            session.removeAttribute("JWT_TOKEN");
            session.removeAttribute("USER_ROLE");
            session.removeAttribute("USERNAME_ADMIN");
            session.removeAttribute("JWT_TOKEN_ADMIN");
            session.removeAttribute("USERNAME_INVENTORY_STAFF");
            session.removeAttribute("JWT_TOKEN_INVENTORY_STAFF");

            session.invalidate(); // Invalidate the session

            System.out.println("Logout successful for user: " + username);
        } else {
            System.out.println("User not found: " + username);
        }

        return "redirect:/login"; // Change to the URL of your home page

    }





    @PostMapping("/admin/toggleUserStatus")
    public String toggleUserStatus(@RequestParam Long userId) {
        User user = userService.getUserById(userId);

        if (user != null) {
            // Toggle user status
            user.setStatus(!user.isStatus());
            userService.updateUser(user);
        }

        return "redirect:/user-list";
    }







}