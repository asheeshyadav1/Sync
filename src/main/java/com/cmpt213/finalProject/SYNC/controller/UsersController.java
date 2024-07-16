package com.cmpt213.finalProject.SYNC.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.util.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cmpt213.finalProject.SYNC.models.UserModel;
import com.cmpt213.finalProject.SYNC.repository.UserRepository;
import com.cmpt213.finalProject.SYNC.service.UsersService;
import com.cmpt213.finalProject.SYNC.service.ImgurService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;


import java.nio.file.Paths;



@Controller
public class UsersController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UsersService userService;

    @Autowired
    private ImgurService imgurService;


    @GetMapping("/")
    public String getHomePage() {
        return "index";
    }

    @GetMapping("/register")
    public String getRegisterPage(Model model) {
        model.addAttribute("user", new UserModel());
        return "register_page";
    }

    @GetMapping("/login")
    public String getLoginPage(Model model, HttpServletRequest request, HttpSession session) {
        UserModel user = (UserModel) session.getAttribute("session_user");
        if (user == null) {
            model.addAttribute("user", new UserModel());
            return "login_page";
        } else {
            model.addAttribute("userLogin", user.getLogin());
            return "personalAccount";
        }

    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute UserModel userModel, Model model, HttpServletRequest request,
            HttpSession session) {
        System.out.println("register request: " + userModel);

        // Hash the password using your custom hash function
        String hashedPassword = UserModel.hashFunc(userModel.getPassword());

        // Set the hashed password in the userModel
        userModel.setPassword(hashedPassword);

        // Hard code gender to be null
        userModel.setGender("not-given");
        userModel.setDob("");
        userModel.setLocation("not-given");
        userModel.setPhoneNumber("");

        // Use the hashed password and null gender in the registration
        UserModel registeredUser = userService.registerUser(userModel.getLogin(), userModel.getPassword(),
                userModel.getEmail(), userModel.getName(), userModel.getGender(), userModel.getDob(),
                userModel.getLocation(), userModel.getPhoneNumber());

        if (registeredUser == null) {
            System.out.println("Registration failed: duplicate user or invalid data");
            return "error_page";
        }

        model.addAttribute("userLogin", userModel.getLogin());
        request.getSession().setAttribute("session_user", userModel);
        return "redirect:/intro";
    }

    @PostMapping("/login")
    public String loginUser(@ModelAttribute UserModel userModel, Model model, HttpServletRequest request,
            HttpSession session) {
        System.out.println("login request: " + userModel);
        String hashedPassword = UserModel.hashFunc(userModel.getPassword());
        userModel.setPassword(hashedPassword);
        UserModel authenticate = userService.authentication(userModel.getLogin(), userModel.getPassword());

        if (authenticate != null) {
            if (authenticate.isActive()) {
                model.addAttribute("userLogin", authenticate.getLogin());
                request.getSession().setAttribute("session_user", authenticate); // Store authenticated user with ID
                return "personalAccount";
            } else {
                model.addAttribute("error", "You have been deactivated. Please contact admin!");
                return "login_page";
            }
        } else {
            model.addAttribute("error", "Invalid login credentials");
            return "login_page";
        }
    }

    @GetMapping("/adminlogin")
    public String getAdminLoginPage(Model model, HttpServletRequest request, HttpSession session) {
        UserModel admin = (UserModel) session.getAttribute("session_admin");
        if (admin == null) {
            model.addAttribute("user", new UserModel());
            return "admin_login_page";
        } else {
            return "redirect:/admin/home";
        }

    }

    @PostMapping("/adminlogin")
    public String adminLogin(@ModelAttribute UserModel userModel, Model model, HttpServletRequest request,
            HttpSession session) {
        System.out.println("admin login request: " + userModel);

        String hashedPassword = UserModel.hashFunc(userModel.getPassword());
        userModel.setPassword(hashedPassword);

        UserModel authenticate = userService.authentication(userModel.getLogin(), userModel.getPassword());

        if (authenticate != null && authenticate.isAdmin()) {
            model.addAttribute("userLogin", userModel.getLogin());
            model.addAttribute("allUsers", userService.getAllUsers());
            request.getSession().setAttribute("session_admin", userModel);
            return "admin_dashboard";
        } else {
            System.out.println("Admin login failed: invalid credentials or not an admin");
            return "error_page";
        }
    }

    @GetMapping("/admin/home")
    public String getAdminHomePage(Model model, HttpServletRequest request, HttpSession session) {
        UserModel admin = (UserModel) session.getAttribute("session_admin");
        if (admin == null) {
            return "redirect:/adminlogin";
        }

        model.addAttribute("allUsers", userService.getAllUsers());
        return "admin_dashboard";

    }

    @GetMapping("/intro")
    public String introPage(Model model, HttpSession session) {
        model.addAttribute("user", (UserModel) session.getAttribute("session_admin"));
        return "introPage";
    }

    @PostMapping("/admin/deactivate/{id}")
    public String deactivateUser(@PathVariable("id") Integer id) {
        userService.deactivateUser(id);
        return "redirect:/admin/home";
    }

    @PostMapping("/admin/activate/{id}")
    public String activateUser(@PathVariable("id") Integer id) {
        userService.activateUser(id);
        return "redirect:/admin/home";
    }

    @GetMapping("/userLogout")
    public String logoutUser(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return "redirect:/login";
    }

    @GetMapping("/checkUsernameAvailability")
    @ResponseBody
    public Map<String, Boolean> checkUsernameAvailability(@RequestParam String username) {
        boolean isUsernameAvailable = !(userRepository.findByLogin(username).isPresent());
        Map<String, Boolean> response = new HashMap<>();
        response.put("available", isUsernameAvailable);
        return response;
    }

    @GetMapping("/getUsersStartingWith")
    @ResponseBody
    public List<UserModel> getUsersStartingWith(@RequestParam String prefix) {
        return userRepository.findByLoginStartingWith(prefix);
    }

    @GetMapping("/userEditAccount")
    public String getEditUserForm(Model model, HttpSession session) {
        UserModel sessionUser = (UserModel) session.getAttribute("session_user");

        if (sessionUser == null || sessionUser.getId() == null) {
            // If no user is in session or ID is null, redirect to login
            return "redirect:/login";
        }

        // Find the user from the database using the ID from the session
        Optional<UserModel> optionalUser = userRepository.findById(sessionUser.getId());
        if (!optionalUser.isPresent()) {
            // If user is not found in the database, redirect to login or an error page
            return "redirect:/login";
        }

        UserModel user = optionalUser.get();
        // Add the user to the model to pre-fill the form
        model.addAttribute("user", user);
        return "editUser"; // Return the view name for the edit user form
    }

    @GetMapping("/seeProfile")
    public String seeProfile(Model model, HttpSession session) {
        UserModel sessionUser = (UserModel) session.getAttribute("session_user");

        if (sessionUser == null || sessionUser.getId() == null) {
            return "redirect:/login";
        }

        // Find the user from the database using the ID from the session
        Optional<UserModel> optionalUser = userRepository.findById(sessionUser.getId());
        if (!optionalUser.isPresent()) {
            return "redirect:/login";
        }

        UserModel user = optionalUser.get();
        model.addAttribute("user", user);
        return "viewProfile";
    }

    @PostMapping("/editUser")
    public String editUser(@ModelAttribute UserModel userModel, Model model, HttpSession session ,@RequestParam("profilePictureFile") MultipartFile profilePictureFile, @RequestParam("resetProfilePicture") boolean resetProfilePicture) throws IOException {
        UserModel sessionUser = (UserModel) session.getAttribute("session_user");

        if (sessionUser == null) {
            // If no user is in session, redirect to login
            return "redirect:/login";
        }
        System.out.println(userModel.getGender());
        // If a profile picture file is provided, upload it and get the URL
        
        
        // Update the user with additional information
        UserModel updatedUser = userService.updateUser(sessionUser.getLogin(), userModel.getDob(),
                userModel.getGender(), userModel.getPhoneNumber(), userModel.getLocation());

        if (updatedUser == null) {
            model.addAttribute("error", "Failed to update user information.");
            return "editUser";
        }

        if (resetProfilePicture) {
            String defaultProfilePictureURL = "/logo/profile logo.png"; // Default profile picture path
            updatedUser.setProfilePictureURL(defaultProfilePictureURL);
            userService.saveUser(updatedUser);
        }
        
        else{
        if (profilePictureFile != null && !profilePictureFile.isEmpty()) {
            // Upload image to Imgur and get the URL
            String profilePictureURL = userService.updateProfilePicture(updatedUser.getLogin(), profilePictureFile);
            // Update the user with the profile picture URL
            updatedUser.setProfilePictureURL(profilePictureURL);
        }
    }

        // Handle profile picture upload if a file is provided
        

        // Update the user in the database with the new profile picture URL
        
        session.setAttribute("session_user", updatedUser);

        model.addAttribute("userLogin", updatedUser.getLogin());
        model.addAttribute("user", updatedUser);

        return "viewProfile";
    }

    // THIS NEEDS TO BE FIXED FOR THE INTRO PAGE
    // DATA HANDLING FOR ADDITIONAL INFO
    @PostMapping("/intro")
    public String getAdditionalInfo(@ModelAttribute UserModel userModel, Model model, HttpSession session, @RequestParam("profilePictureFile") MultipartFile profilePictureFile) throws IOException {
        UserModel sessionUser = (UserModel) session.getAttribute("session_user");

        if (sessionUser == null) {
            model.addAttribute("error", "Session expired. Please log in again.");
            return "login_page";
        }

        UserModel updatedUser = userService.updateUser(sessionUser.getLogin(), userModel.getDob(),
                userModel.getGender(), userModel.getPhoneNumber(), userModel.getLocation());

        if (updatedUser == null) {
            model.addAttribute("error", "Failed to update user information.");
            return "introPage";
        }

        userRepository.save(updatedUser);

        session.setAttribute("session_user", updatedUser);

        model.addAttribute("userLogin", updatedUser.getLogin());
        model.addAttribute("user", updatedUser);

        return "personalAccount";
    }

    // need to implement the backend for deleting the user
    @GetMapping("/delete")
    public String delUser(HttpSession session, Model model) {
        UserModel sessionUser = (UserModel) session.getAttribute("session_user");

        if (sessionUser == null || sessionUser.getId() == null) {
            // If no user is in session or ID is null, redirect to login
            return "redirect:/login";
        }

        // Delete the user from the database
        userService.deleteUserById(sessionUser.getId());

        // Invalidate the session after deletion
        session.invalidate();

        // Add a message to the model to be displayed on the confirmation page
        model.addAttribute("message", "Your account has been successfully deleted.");

        return "delete_confirmation"; // Return the view name for the delete confirmation page
    }

    @GetMapping("/adminlogout")
    public String logoutAdmin(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return "redirect:/adminlogin";
    }

    @GetMapping("/getSendRequestFriends")
    @ResponseBody
    public List<Integer> sendRequestUsers(HttpSession session) {
        UserModel sessionUser = (UserModel) session.getAttribute("session_user");
        sessionUser = userService.findByIdWithFriendRequests(sessionUser.getId().longValue());
        return userService.findRequestedFriends(sessionUser).stream().map(UserModel::getId)
                .collect(Collectors.toList());
    }

    @PostMapping("/sendRequest/{id}")
    @ResponseBody
    public Map<String, String> sendRequest(@PathVariable Integer id, HttpSession session) {
        UserModel sessionUser = (UserModel) session.getAttribute("session_user");
        sessionUser = userService.findByIdWithFriendRequests(sessionUser.getId().longValue());
        boolean requestSent = userService.sendFriendRequest(id, sessionUser);
        Map<String, String> response = new HashMap<>();
        if (requestSent) {
            response.put("status", "Request Sent");
        } else {
            boolean requestDeleted = userService.deleteFriendRequest(sessionUser.getId(), id);
            if (requestDeleted) {
                response.put("status", "Request Deleted");
            } else {
                response.put("status", "Failed to delete request");
            }
        }
        return response;
    }

}
