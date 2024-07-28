package com.cmpt213.finalProject.SYNC.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

// import com.cmpt213.finalProject.SYNC.models.ChatMessage;
import com.cmpt213.finalProject.SYNC.models.UserFriendKey;
import com.cmpt213.finalProject.SYNC.models.UserModel;
import com.cmpt213.finalProject.SYNC.models.UserPost;
import com.cmpt213.finalProject.SYNC.repository.UserRepo;
import com.cmpt213.finalProject.SYNC.repository.UserRepository;
// import com.cmpt213.finalProject.SYNC.service.ChatMessageService;
import com.cmpt213.finalProject.SYNC.service.ImgurService;
import com.cmpt213.finalProject.SYNC.service.PostService;
import com.cmpt213.finalProject.SYNC.service.UsersService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Controller
public class UsersController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UsersService userService;

    @Autowired
    private PostService postService;

    @Autowired
    private ImgurService imgurService;

    @Autowired
    private UserRepo userRepoMaps;

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
    public String getLoginPage(Model model, HttpServletRequest request, HttpSession session,
            HttpServletResponse response) {
        UserModel user = (UserModel) session.getAttribute("session_user");
        if (user == null) {
            model.addAttribute("user", new UserModel());

            // Set headers to prevent caching
            response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1
            response.setHeader("Pragma", "no-cache"); // HTTP 1.0
            response.setDateHeader("Expires", 0); // Proxies

            return "login_page";
        } else {
            model.addAttribute("userLogin", user.getLogin());
            postService.getRecentFriendPosts(user.getId());
            System.out.println("\n\n\n\n" + postService.getRecentFriendPosts(user.getId()) + "\n\n\n\n");
            model.addAttribute("posts", user.getLogin());

            // Set a cookie with the session token
            Cookie sessionCookie = new Cookie("session", session.getId());
            sessionCookie.setHttpOnly(true);
            sessionCookie.setPath("/");
            response.addCookie(sessionCookie);

            return "personalAccount";
        }
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute UserModel userModel, Model model, HttpServletRequest request,
            HttpSession session) throws IOException {
        System.out.println("register request: " + userModel);

        String hashedPassword = UserModel.hashFunc(userModel.getPassword());

        userModel.setPassword(hashedPassword);

        userModel.setGender("not-given");
        userModel.setDob("");
        userModel.setLocation("not-given");
        userModel.setPhoneNumber("");
        userModel.setProfilePictureURL("");
        userModel.setLongitude(0.0);
        userModel.setLatitude(0.0);

        UserModel registeredUser = userService.registerUser(userModel.getLogin(), userModel.getPassword(),
                userModel.getEmail(), userModel.getName(), userModel.getGender(), userModel.getDob(),
                userModel.getLocation(), userModel.getPhoneNumber(), userModel.getProfilePictureURL(),
                userModel.getLatitude(), userModel.getLongitude());

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
        String hashedPassword = UserModel.hashFunc(userModel.getPassword());
        userModel.setPassword(hashedPassword);
        UserModel authenticate = userService.authentication(userModel.getLogin(), userModel.getPassword());

        if (authenticate != null) {
            if (authenticate.isActive()) {
                model.addAttribute("userLogin", authenticate.getLogin());
                request.getSession().setAttribute("session_user", authenticate); // Store authenticated user with ID

                // Use a TreeSet to automatically sort posts and avoid duplicates
                Set<UserPost> uniquePosts = new TreeSet<>(Comparator.comparing(UserPost::getPublishTime).reversed());
                uniquePosts.addAll(postService.getRecentFriendPosts(authenticate.getId()));

                // Convert back to list if necessary
                List<UserPost> sortedPosts = new ArrayList<>(uniquePosts);
                model.addAttribute("posts", sortedPosts);

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

        // Clear the session cookie
        Cookie sessionCookie = new Cookie("session", null);
        sessionCookie.setPath("/*");
        sessionCookie.setMaxAge(0);
        response.addCookie(sessionCookie);

        // Set headers to prevent caching
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1
        response.setHeader("Pragma", "no-cache"); // HTTP 1.0
        response.setDateHeader("Expires", 0); // Proxies

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

    @GetMapping("/getUsersExcludingSession")
    @ResponseBody
    public List<UserModel> getUsersExcludingSession(HttpSession session) {
        UserModel sessionUser = (UserModel) session.getAttribute("session_user");
        return userService.findAllUsersExcludingSessionUser(sessionUser.getId());
    }

    @GetMapping("/getUsersStartingWith")
    @ResponseBody
    public List<UserModel> getUsersStartingWith(@RequestParam String prefix, HttpSession session) {
        UserModel sessionUser = (UserModel) session.getAttribute("session_user");
        if (sessionUser == null) {
            return List.of();
        }

        // Fetch the users from the service
        List<UserModel> users = userService.findAllUsersStartingWithExcludingFriends(prefix, sessionUser.getId());

        // Limit the number of users to 10
        return users.stream().limit(10).collect(Collectors.toList());
    }

    @GetMapping("/userEditAccount")
    public String getEditUserForm(Model model, HttpSession session) {
        UserModel sessionUser = (UserModel) session.getAttribute("session_user");

        if (sessionUser == null || sessionUser.getId() == null) {
            return "redirect:/login";
        }

        Optional<UserModel> optionalUser = userRepository.findById(sessionUser.getId());
        if (!optionalUser.isPresent()) {
            return "redirect:/login";
        }

        UserModel user = optionalUser.get();
        model.addAttribute("user", user);
        return "editUser";
    }

    @GetMapping("/seeProfile")
    public String seeProfile(Model model, HttpSession session) {
        UserModel sessionUser = (UserModel) session.getAttribute("session_user");

        if (sessionUser == null || sessionUser.getId() == null) {
            return "redirect:/login";
        }

        // Find the user from the database using the ID from the session
        UserModel user = userService.findByIdWithFriendRequests(sessionUser.getId().longValue());
        if (user == null) {
            return "redirect:/login";
        }

        // Fetch user posts
        List<UserPost> userPosts = postService.getUserPosts(user.getId());

        model.addAttribute("user", user);
        model.addAttribute("userPosts", userPosts);
        return "viewProfile";
    }

    @PostMapping("/editUser")
    public String editUser(@ModelAttribute UserModel userModel, Model model, HttpSession session,
            @RequestParam("profilePictureFile") MultipartFile profilePictureFile,
            @RequestParam("resetProfilePicture") boolean resetProfilePicture) throws IOException {
        UserModel sessionUser = (UserModel) session.getAttribute("session_user");

        if (sessionUser == null) {
            return "redirect:/login";
        }
        System.out.println(userModel.getGender());

        UserModel updatedUser = userService.updateUser(sessionUser.getLogin(), userModel.getDob(),
                userModel.getGender(), userModel.getPhoneNumber(), userModel.getLocation(), userModel.getLatitude(),
                userModel.getLongitude());

        if (updatedUser == null) {
            model.addAttribute("error", "Failed to update user information.");
            return "editUser";
        }

        if (resetProfilePicture) {
            String defaultProfilePictureURL = "/logo/profile logo.png"; // Default profile picture path
            updatedUser.setProfilePictureURL(defaultProfilePictureURL);
            userService.saveUser(updatedUser);
        }

        else {
            if (profilePictureFile != null && !profilePictureFile.isEmpty()) {
                String profilePictureURL = userService.updateProfilePicture(updatedUser.getLogin(), profilePictureFile);
                updatedUser.setProfilePictureURL(profilePictureURL);
            }
        }

        session.setAttribute("session_user", updatedUser);

        model.addAttribute("userLogin", updatedUser.getLogin());
        model.addAttribute("user", updatedUser);

        List<UserPost> userPosts = postService.getUserPosts(updatedUser.getId());
        model.addAttribute("userPosts", userPosts);

        return "viewProfile";
    }

    @PostMapping("/intro")
    public String getAdditionalInfo(@ModelAttribute UserModel userModel, Model model, HttpSession session,
            @RequestParam("profilePictureFile") MultipartFile profilePictureFile) throws IOException {
        UserModel sessionUser = (UserModel) session.getAttribute("session_user");

        if (sessionUser == null) {
            model.addAttribute("error", "Session expired. Please log in again.");
            return "login_page";
        }

        UserModel updatedUser = userService.updateUser(sessionUser.getLogin(), userModel.getDob(),
                userModel.getGender(), userModel.getPhoneNumber(), userModel.getLocation(), userModel.getLatitude(),
                userModel.getLongitude());

        if (updatedUser == null) {
            model.addAttribute("error", "Failed to update user information.");
            return "introPage";
        }

        if (profilePictureFile.isEmpty()) {
            String defaultProfilePictureURL = "/logo/profile logo.png"; // Default profile picture path
            updatedUser.setProfilePictureURL(defaultProfilePictureURL);
            userService.saveUser(updatedUser);
        }

        if (profilePictureFile != null && !profilePictureFile.isEmpty()) {
            String profilePictureURL = userService.updateProfilePicture(updatedUser.getLogin(), profilePictureFile);
            updatedUser.setProfilePictureURL(profilePictureURL);
        }

        userRepository.save(updatedUser);

        session.setAttribute("session_user", updatedUser);

        model.addAttribute("userLogin", updatedUser.getLogin());
        model.addAttribute("user", updatedUser);

        return "personalAccount";
    }

    @GetMapping("/delete")
    public String delUser(HttpSession session, Model model) {
        UserModel sessionUser = (UserModel) session.getAttribute("session_user");

        if (sessionUser == null || sessionUser.getId() == null) {
            return "redirect:/login";
        }

        userService.deleteUserByIdAndRemoveFromFriends(sessionUser.getId());

        session.invalidate();

        model.addAttribute("message", "Your account has been successfully deleted.");

        return "delete_confirmation";
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
        return userService.findRequestedFriendIds(sessionUser);
    }

    @PostMapping("/sendRequest/{id}")
    @ResponseBody
    public Map<String, String> sendRequest(@PathVariable Integer id, HttpSession session) {
        UserModel sessionUser = (UserModel) session.getAttribute("session_user");

        sessionUser = userService.findByIdWithFriendRequests(sessionUser.getId().longValue());
        UserModel requser = userService.findByIdWithFriendRequests(id.longValue());

        boolean requestSent = userService.sendFriendRequest(id, sessionUser);
        // boolean reqUser = userService.sendFriendRequest(sessionUser.getId(),
        // requser);

        Map<String, String> response = new HashMap<>();
        if (requestSent) {
            response.put("status", "Request Sent");
        } else {
            boolean requestDeleted = userService.deleteFriendRequest(sessionUser.getId(), id);
            // boolean reqdeleted = userService.deleteFriendRequest(id,
            // sessionUser.getId());
            if (requestDeleted) {
                response.put("status", "Request Deleted");
            } else {
                response.put("status", "Failed to delete request");
            }
        }
        return response;
    }

    @PostMapping("/acceptRequest/{id}")
    @ResponseBody
    public Map<String, String> acceptRequest(@PathVariable Integer id, HttpSession session) {

        UserModel sessionUser = (UserModel) session.getAttribute("session_user");
        System.out.println("\n\n\n\n" + id + "   \n\n" + sessionUser.getId() + "\n\n\n");
        boolean requestAccepted = userService.acceptFriendRequest(sessionUser.getId(), id);
        Map<String, String> response = new HashMap<>();
        if (requestAccepted) {
            response.put("status", "Request Accepted");
        } else {
            response.put("status", "Failed to accept request");
        }
        return response;
    }

    @PostMapping("/declineRequest/{id}")
    @ResponseBody
    public Map<String, String> declineRequest(@PathVariable Integer id, HttpSession session) {
        UserModel sessionUser = (UserModel) session.getAttribute("session_user");
        boolean requestDeclined = userService.declineFriendRequest(sessionUser.getId(), id);
        Map<String, String> response = new HashMap<>();
        if (requestDeclined) {
            response.put("status", "Request Declined");
        } else {
            response.put("status", "Failed to decline request");
        }
        return response;
    }

    @GetMapping("/getFriendRequests")
    @ResponseBody
    public List<UserModel> getFriendRequests(HttpSession session) {
        UserModel sessionUser = (UserModel) session.getAttribute("session_user");
        sessionUser = userService.findByIdWithFriendRequests(sessionUser.getId().longValue());
        return userService.findGotFriendRequests(sessionUser);
    }

    @GetMapping("/users/view")
    public String getAllUsers(Model model) {
        System.out.println("Getting all users");

        List<UserModel> users = userRepository.findAll();
        // end of database call
        model.addAttribute("us", users);
        return "showAll";
    }

    // @GetMapping("/maps")
    // public String getMaps(Model model) {
    // Map<String, String> friendLocation = new HashMap<>();
    // friendLocation.put("location", "New York");
    // model.addAttribute("friendLocation", friendLocation);
    // return "Maps";
    // }

    @GetMapping("/getLocation")
    public String getLocation(Model model, HttpSession session) {
        UserModel sessionUser = (UserModel) session.getAttribute("session_user");

        if (sessionUser == null) {
            return "redirect:/login";
        }

        UserModel user = userRepository.findById(sessionUser.getId()).orElse(null);
        if (user == null) {
            return "redirect:/login";
        }

        // Assuming you have a method to retrieve all friends
        List<UserFriendKey> friends = user.getFriends();

        // Extract friends' locations
        List<Map<String, Object>> friendsLocations = friends.stream().map(friend -> {
            UserModel friendUser = userRepository.findById(friend.getFriendId()).orElse(null);
            if (friendUser != null) {
                Map<String, Object> locationData = new HashMap<>();
                locationData.put("login", friendUser.getLogin());
                locationData.put("location", friendUser.getLocation());
                locationData.put("latitude", friendUser.getLatitude());
                locationData.put("longitude", friendUser.getLongitude());
                return locationData;
            }
            return null;
        }).filter(Objects::nonNull).collect(Collectors.toList());

        model.addAttribute("user", user);
        model.addAttribute("friends", friends);
        model.addAttribute("friendsLocations", friendsLocations);

        return "Maps";
    }

    // @GetMapping("/friends")
    // public String getFriends(Model model, HttpSession session) {
    // UserModel sessionUser = (UserModel) session.getAttribute("session_user");

    // if (sessionUser == null) {
    // return "redirect:/login";
    // }

    // UserModel user = userRepository.findById(sessionUser.getId()).orElse(null);
    // if (user == null) {
    // return "redirect:/login";
    // }

    // // Assuming you have a method to retrieve all friends
    // List<UserFriendKey> friends = user.getFriends();

    // // Extract friends' locations
    // List<Map<String, Object>> friendMessages = friends.stream()
    // .map(friend -> {
    // UserModel friendUser =
    // userRepository.findById(friend.getFriendId()).orElse(null);
    // if (friendUser != null) {
    // Map<String, Object> friendsmessage = new HashMap<>();
    // friendsmessage.put("login", friendUser.getLogin());
    // friendsmessage.put("Id", friendUser.getId());
    // return friendsmessage;
    // }
    // return null;
    // })
    // .filter(Objects::nonNull)
    // .collect(Collectors.toList());

    // model.addAttribute("user", user);
    // model.addAttribute("friends", friends);
    // model.addAttribute("friendMessages", friendMessages);

    // return "friends"; // Return the name of the view template
    // }

    // @PostMapping("/sendMessage")
    // public String sendMessage(Model model, HttpSession session, @RequestParam
    // Integer receiverId, @RequestParam String content) {
    // UserModel sessionUser = (UserModel) session.getAttribute("session_user");

    // if (sessionUser == null) {
    // return "redirect:/login";
    // }

    // UserModel user = userRepository.findById(sessionUser.getId()).orElse(null);
    // if (user == null) {
    // return "redirect:/login";
    // }

    // List<UserModel> receiver_all =
    // userRepository.findByFriendsUserId(receiverId);

    // UserModel receiver = receiver_all.get(0);

    // if (receiver != null) {
    // chatMessageService.sendMessage(user, receiver, content);
    // } else {
    // // Handle the case where the receiver is not found
    // model.addAttribute("error", "Receiver not found");
    // return "errorPage"; // Redirect to an error page or handle accordingly
    // }

    // return "redirect:/directMessaging"; // Redirect to the direct messaging page
    // }

    @GetMapping("/Dm")
    public String getFriends(Model model, HttpSession session) {
        UserModel sessionUser = (UserModel) session.getAttribute("session_user");

        if (sessionUser == null) {
            return "redirect:/login";
        }

        UserModel user = userRepository.findById(sessionUser.getId()).orElse(null);
        if (user == null) {
            return "redirect:/login";
        }

        // Assuming you have a method to retrieve all friends
        List<UserFriendKey> friends = user.getFriends();

        // Use a HashSet to avoid duplicate friend logins
        Set<Map<String, Object>> friendMessages = new HashSet<>();

        friends.forEach(friend -> {
            UserModel friendUser = userRepository.findById(friend.getFriendId()).orElse(null);
            if (friendUser != null) {
                Map<String, Object> friendsMessage = new HashMap<>();
                friendsMessage.put("login", friendUser.getLogin());
                friendsMessage.put("id", friendUser.getId());

                // Add to the set to ensure uniqueness
                friendMessages.add(friendsMessage);
            }
        });

        System.out.println("\n\n\n\n" + friendMessages + "\n\n\n\n");

        model.addAttribute("user", user);
        model.addAttribute("friendMessages", friendMessages);

        return "message"; // Return the name of the view template
    }

}
