// DashboardController.java
package admin_user.controller ;

import admin_user.dto.SignupRequest;
import admin_user.model.Category;
import admin_user.model.User;
import admin_user.repositories.UserRepo;
import admin_user.service.CategoryService;
import admin_user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
@RequestMapping(value = "/pages")
public class DashboardController {
    @Autowired
    private UserRepo userRepo;



    @GetMapping(value = "/dashboard")
    public ModelMap dashboard() {
        return new ModelMap();
    }

    @GetMapping(value = "/categories")
    public ModelMap categories() {
        return new ModelMap();
    }

    @GetMapping(value = "/subcategories")
    public ModelMap adminSubCategory(){
        return new ModelMap();
    }

    @GetMapping(value = "/addproduct")
    public ModelMap adminProduct(){
        return new ModelMap();
    }

    @GetMapping(value = "/brand")
    public ModelMap adminBrand(){
        return new ModelMap();
    }

    @GetMapping(value = "/viewproduct")
    public ModelMap adminViewProduct(){
        return new ModelMap();
    }

    @GetMapping(value = "/addInventory")
    public ModelMap adminaddInventory(){
        return new ModelMap();
    }

    @GetMapping(value = "/viewinventory")
    public ModelMap adminviewInventory(){
        return new ModelMap();
    }

    @GetMapping(value = "/pos")
    public ModelMap pos(){
        return new ModelMap();
    }


    @GetMapping(value = "/invoice")
    public ModelMap invoice(){
        return new ModelMap();
    }

    @Autowired
    UserService userService;



    @GetMapping("/user-list")
    public String getAllUsers(Model model) {
        List<User> userList = userService.getAllUsers();

        model.addAttribute("users", userList);
        return "pages/user-list"; // Create a new HTML page (user-list.html) to display the user table
    }
    @GetMapping("/user-profile")
    public String showUserProfile(Model model, HttpServletRequest request) {
        // Retrieve username and role from session
        HttpSession session = request.getSession();
        String role = (String) session.getAttribute("USER_ROLE");
        String username = (String) session.getAttribute("USERNAME_" + role); // Retrieve username using role-specific attribute name

        // Add username and role to the model
        model.addAttribute("username", username);
        model.addAttribute("role", role);

        // Return the view name of the user profile page
        return "pages/user-profile";
    }


    @GetMapping("/StaffRegistration")
    public String getStaffRegistrationPage(@ModelAttribute("user") SignupRequest signupRequest) {
        return "pages/StaffRegistration";
    }

    @PostMapping("/StaffRegistration")
    public String saveStaff(@ModelAttribute("user") SignupRequest signupRequest, Model model) {
        if (userService.registerUser(signupRequest) != null) {
            return "redirect:/pages/dashboard"; // Redirect to the login page on successful staff registration
        }
        model.addAttribute("message", "Registered Successfully!");
        return "pages/StaffRegistration";
    }



}

