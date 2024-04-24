package admin_user.controller;


import admin_user.dto.SignupRequest;

import admin_user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;



@Controller


public class RegistrationController {
    @Autowired
    private UserService userService;

    @GetMapping("/registration")
    public String getRegistrationPage(@ModelAttribute("user") SignupRequest signupRequest) {
        return "pages/register";
    }

    @PostMapping ("/registration")
    public String save(@ModelAttribute SignupRequest signupRequest, Model model) {
        if (userService.registerUser(signupRequest) != null) {
            return "redirect:/login"; // Redirect to the login page on successful registration
        }
        return "register";
    }

















}