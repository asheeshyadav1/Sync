package com.cmpt213.finalProject.SYNC.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cmpt213.finalProject.SYNC.models.*;
import com.cmpt213.finalProject.SYNC.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;

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
                user.getRequests().add(new UserFriendRequestKey(user.getId(), sessionUser.getId()));
                userRepository.save(sessionUser);
                return true;
            }
        }
        return false;
    }

    @Transactional
    public boolean deleteFriendRequest(Integer userId, Integer friendRequestId) {
        UserModel user = userRepository.findById(userId).orElse(null);
        UserModel sentUser = userRepository.findById(friendRequestId).orElse(null);
        if (user != null) {
            UserFriendRequestKey key = new UserFriendRequestKey(userId, friendRequestId);
            UserFriendRequestKey key1 = new UserFriendRequestKey(friendRequestId, userId);
            if (user.getFriendRequests().remove(key) && sentUser.getRequests().remove(key1)) {
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
                .filter(user -> user != null).collect(Collectors.toList());
    }

    @Transactional
    public List<UserModel> findFriendRequests(UserModel sessionUser){
        List<UserFriendRequestKey> friendRequestIds = sessionUser.getRequests();
        return friendRequestIds.stream()
                .map(key -> userRepository.findById(key.getFriendRequestId().longValue()).orElse(null))
                .filter(user -> user != null).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserModel> findAllUsersExcludingSessionUser(UserModel sessionUser) {
        List<UserFriendRequestKey> friendRequestIds = sessionUser.getRequests();

        return friendRequestIds.stream()
                .map(key -> userRepository.findById(key.getFriendRequestId().longValue()).orElse(null))
                .filter(user -> !user.getId().equals(sessionUser.getId())).collect(Collectors.toList());

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

    @Transactional
    public boolean declineFriendRequest(Integer userId, Integer friendRequestId) {
        return deleteFriendRequest(friendRequestId, userId);
    }

    @Transactional
    public boolean acceptFriendRequest(Integer userId, Integer friendRequestId) {
        UserModel user = userRepository.findById(userId).orElse(null);
        UserModel friend = userRepository.findById(friendRequestId).orElse(null);
        if (user != null && friend != null) {
            // Remove the friend request
            UserFriendRequestKey key = new UserFriendRequestKey(friendRequestId, userId);
            UserFriendRequestKey key1 = new UserFriendRequestKey(userId,friendRequestId );
            if (friend.getFriendRequests().remove(key) && user.getRequests().remove(key1)) {
                // Add to friends list
                user.getFriends().add(new UserFriendKey(userId, friendRequestId));
                friend.getFriends().add(new UserFriendKey(friendRequestId, userId));
                userRepository.save(user);
                userRepository.save(friend);
                return true;
            }
        }
        return false;
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
}
