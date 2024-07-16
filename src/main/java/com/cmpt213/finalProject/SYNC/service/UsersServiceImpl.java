package com.cmpt213.finalProject.SYNC.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cmpt213.finalProject.SYNC.models.*;
import com.cmpt213.finalProject.SYNC.repository.UserRepository;
import org.springframework.web.multipart.MultipartFile;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.List;
import java.util.Objects;
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
    public UserModel registerUser(String login, String password, String email, String name, String gender, String dob,
            String location, String phoneNumber) {
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
    public void activateUser(Integer id) {
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

    public void saveUser(UserModel user) {
        userRepository.save(user);
    }

    public void deleteUserById(Integer userId) {
        userRepository.deleteById(userId);
    }

    @Transactional
    public void deleteUserByIdAndRemoveFromFriends(Integer userId) {
        UserModel userToDelete = userRepository.findById(userId).orElse(null);
        if (userToDelete != null) {
            List<UserFriendKey> friends = userToDelete.getFriends();

            for (UserFriendKey friendKey : friends) {
                UserModel friend = userRepository.findById(friendKey.getFriendId()).orElse(null);
                if (friend != null) {
                    friend.getFriends().removeIf(fk -> fk.getFriendId().equals(userId));
                    userRepository.save(friend);
                }
            }

            userRepository.deleteById(userId);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserModel> findAllUsersStartingWithExcludingFriends(String prefix, Integer sessionUserId) {
        UserModel sessionUser = userRepository.findById(sessionUserId).orElse(null);
        if (sessionUser == null) {
            return List.of();
        }
        List<Integer> friendIds = sessionUser.getFriends().stream()
            .map(UserFriendKey::getFriendId)
            .collect(Collectors.toList());
        return userRepository.findByLoginStartingWith(prefix).stream()
            .filter(user -> !friendIds.contains(user.getId()))
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserModel> findAllUsersExcludingSessionUser(Integer sessionUserId) {
        return userRepository.findAll().stream().filter(user -> !user.getId().equals(sessionUserId))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public UserModel findByIdWithFriendRequests(Long id) {
        Optional<UserModel> userOpt = userRepository.findById(id);
        if (userOpt.isPresent()) {
            UserModel user = userOpt.get();
            user.getFriendRequests().size();
            user.getGotFriendRequests().size();
            return user;
        }
        return null;
    }

    @Transactional(readOnly = true)
    public List<UserModel> findGotFriendRequests(UserModel sessionUser) {
        List<UserFriendRequestKey> gotFriendRequestIds = sessionUser.getGotFriendRequests();

        return gotFriendRequestIds.stream().map(key -> userRepository.findById(key.getFriendRequestId()).orElse(null))
                .filter(Objects::nonNull).collect(Collectors.toList());
    }

    @Transactional
    public boolean sendFriendRequest(Integer id, UserModel sessionUser) {
        Optional<UserModel> optionalUser = userRepository.findById(id);
        if (optionalUser.isPresent()) {
            UserModel user = optionalUser.get();
            UserFriendRequestKey friendRequestKey = new UserFriendRequestKey(sessionUser.getId(), user.getId());
            if (!sessionUser.getFriendRequests().contains(friendRequestKey)) {
                sessionUser.getFriendRequests().add(friendRequestKey);
                user.getGotFriendRequests().add(new UserFriendRequestKey(user.getId(), sessionUser.getId()));
                userRepository.save(sessionUser);
                userRepository.save(user);
                return true;
            }
        }
        return false;
    }

    @Transactional
    public boolean deleteFriendRequest(Integer userId, Integer friendRequestId) {
        UserModel user = userRepository.findById(userId).orElse(null);
        if (user != null) {
            UserFriendRequestKey friendRequestKey = new UserFriendRequestKey(userId, friendRequestId);
            if (user.getFriendRequests().remove(friendRequestKey)) {
                userRepository.save(user);
                UserModel friend = userRepository.findById(friendRequestId).orElse(null);
                if (friend != null) {
                    friend.getGotFriendRequests().remove(new UserFriendRequestKey(friendRequestId, userId));
                    userRepository.save(friend);
                }
                return true;
            }
        }
        return false;
    }

    @Transactional
    public boolean acceptFriendRequest(Integer userId, Integer friendRequestId) {
        UserModel user = userRepository.findById(userId).orElse(null);
        UserModel friend = userRepository.findById(friendRequestId).orElse(null);

        if (user != null && friend != null) {
            UserFriendRequestKey friendRequestKey = new UserFriendRequestKey(userId, friendRequestId);
            if (user.getGotFriendRequests().remove(friendRequestKey)) {

                System.out.println(user.getEmail());
                System.out.println(friend.getEmail());

                user.getFriends().add(new UserFriendKey(userId, friendRequestId));
                friend.getFriends().add(new UserFriendKey(friendRequestId, userId));
                userRepository.save(user);
                userRepository.save(friend);
                return true;
            }
        }
        return false;
    }

    @Transactional
    public boolean declineFriendRequest(Integer userId, Integer friendRequestId) {
        UserModel user = userRepository.findById(userId).orElse(null);

        if (user != null) {
            UserFriendRequestKey friendRequestKey = new UserFriendRequestKey(userId, friendRequestId);
            if (user.getGotFriendRequests().remove(friendRequestKey)) {
                userRepository.save(user);
                return true;
            }
        }
        return false;
    }

    @Transactional(readOnly = true)
    public List<Integer> findRequestedFriendIds(UserModel sessionUser) {
        List<UserFriendRequestKey> friendRequestIds = sessionUser.getFriendRequests();

        return friendRequestIds.stream().map(UserFriendRequestKey::getFriendRequestId).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public boolean hasSentFriendRequest(Integer userId, Integer friendRequestId) {
        UserModel user = userRepository.findById(userId).orElse(null);
        if (user != null) {
            return user.getFriendRequests().contains(new UserFriendRequestKey(userId, friendRequestId));
        }
        return false;
    }
}
