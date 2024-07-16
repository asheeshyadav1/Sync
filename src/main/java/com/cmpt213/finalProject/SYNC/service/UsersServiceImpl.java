package com.cmpt213.finalProject.SYNC.service;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import com.cmpt213.finalProject.SYNC.models.*;
import com.cmpt213.finalProject.SYNC.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;
import java.io.IOException;


import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;


import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
public class UsersServiceImpl implements UsersService {

    @Autowired
    private UserRepository userRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private ImgurService imgurService;

    
    @Override
    public UserModel registerUser(String login, String password, String email, String name,  String gender, String dob, String location, String phoneNumber) {
        if (login == null || password == null) {
            System.out.println("Registration failed: login or password is null");
            return null;
        } else {
            if (userRepository.findByLogin(login).isPresent()) {
                System.out.println("Duplicate User");
                return null;
            }

            UserModel user = new UserModel();
            user.setLogin(login);
            user.setPassword(password);
            user.setEmail(email);
            user.setName(name);
            user.setGender(gender);
            user.setDob(dob);
            user.setLocation(location);
            user.setPhoneNumber(phoneNumber);
            

            return userRepository.save(user);
        }
    }

    @Override
    public UserModel authentication(String login, String password) {
        return userRepository.findByLoginAndPassword(login, password).orElse(null);
    }

    @Override
    public List<UserModel> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public void deactivateUser(Integer id) {
        UserModel user = userRepository.findById(id).orElse(null);
        if (user != null) {
            user.setActive(false);
            userRepository.save(user);
        }
    }

    @Override
    public void activateUser(Integer id){
        UserModel user = userRepository.findById(id).orElse(null);
        if (user != null) {
            user.setActive(true);
            userRepository.save(user);
        }
    }
    
    // Method to update user information
    public UserModel updateUser(String login, String dob, String gender, String phoneNumber, String location) {
        Optional<UserModel> optionalUser = userRepository.findByLogin(login);

        System.out.println(login);
    
        if (optionalUser.isPresent()) {
            UserModel user = optionalUser.get();
            // Update the user fields
            user.setDob(dob);
            user.setGender(gender);
            user.setPhoneNumber(phoneNumber);
            user.setLocation(location);

            // Save the updated user back to the repository
            userRepository.save(user);
            return user;
        }
        return null; // Handle case where user is not found
    }

    public String updateProfilePicture(String login, MultipartFile image) {
        UserModel user = userRepository.findByLogin(login).orElseThrow(() -> new RuntimeException("User not found"));
        String ppURL= imgurService.uploadImage(image);
        if (user != null) {
            // Update the user's profile picture URL
            user.setProfilePictureURL(ppURL);

            // Save the updated user back to the database
            userRepository.save(user);
        }

        return ppURL;
    }

    public void deleteUserById(Integer userId) {
        userRepository.deleteById(userId);
    }

    @Transactional(readOnly = true)
    public UserModel findByIdWithFriendRequests(Long id) {
        Optional<UserModel> userOpt = userRepository.findById(id);
        if (userOpt.isPresent()) {
            UserModel user = userOpt.get();
            user.getFriendRequests().size(); // Force initialization
            return user;
        }
        return null;
    }

    @Transactional
    public boolean sendFriendRequest(Integer id, UserModel sessionUser) {
        Optional<UserModel> optionalUser = userRepository.findById(id);
        if (optionalUser.isPresent()) {
            UserModel user = optionalUser.get();
            UserFriendRequestKey friendRequestKey = new UserFriendRequestKey(sessionUser.getId(), user.getId());
            if (!sessionUser.getFriendRequests().contains(friendRequestKey)) {
                sessionUser.getFriendRequests().add(friendRequestKey);
                userRepository.save(sessionUser);
                return true;
            }
        }
        return false;
    }

    @Transactional
    public boolean deleteFriendRequest(Integer userId, Integer friendRequestId) {
        UserModel user = userRepository.findById(userId).orElse(null);
        if (user != null) {
            UserFriendRequestKey key = new UserFriendRequestKey(userId, friendRequestId);
            if (user.getFriendRequests().remove(key)) {
                userRepository.save(user);
                return true;
            }
        }
        return false;
    }

    @Transactional(readOnly = true)
    public List<UserModel> findRequestedFriends(UserModel sessionUser) {
        List<UserFriendRequestKey> friendRequestIds = sessionUser.getFriendRequests();
        return friendRequestIds.stream()
                .map(key -> userRepository.findById(key.getFriendRequestId().longValue()).orElse(null))
                .filter(user -> user != null)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public boolean hasSentFriendRequest(Integer userId, Integer friendRequestId) {
        UserModel user = userRepository.findById(userId).orElse(null);
        if (user != null) {
            UserFriendRequestKey key = new UserFriendRequestKey(userId, friendRequestId);
            return user.getFriendRequests().contains(key);
        }
        return false;
    }
}
