package admin_user.service;


import admin_user.model.User;
import admin_user.repositories.UserRepo;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class CustomSuccessHandler implements AuthenticationSuccessHandler  {



@Autowired
private UserRepo userRepository;

    @Autowired
    private UserService userService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        System.out.println("onAuthenticationSuccess get called");

        String redirectUrl = null;

        if (authentication.getPrincipal() instanceof DefaultOAuth2User) {
            DefaultOAuth2User defaultOAuth2User = (DefaultOAuth2User) authentication.getPrincipal();
            String username = defaultOAuth2User.getAttribute("email") != null ? defaultOAuth2User.getAttribute("email")
                    : defaultOAuth2User.getAttribute("login") + "@gmail.com";

            if (userRepository.findByEmail(username) == null) {
                User user = new User();

                user.setEmail(username);

                user.setName(defaultOAuth2User.getAttribute("email") != null
                        ? defaultOAuth2User.getAttribute("email").toString().replace("@gmail.com", "")
                        : defaultOAuth2User.getAttribute("login"));
                userRepository.save(user);
            }
            redirectUrl = "/dashboard";
        }else {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            User users = userRepository.findByEmail(userDetails.getUsername());
            String output = userService.generateOtp(users);
            if(output == "success") {
                redirectUrl = "getOtp";
            }
        }
        new DefaultRedirectStrategy().sendRedirect(request, response, redirectUrl);

    }





}

