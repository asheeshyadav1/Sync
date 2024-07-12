package com.cmpt213.finalProject.SYNC.service;

import com.cmpt213.finalProject.SYNC.models.UserModel;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public interface UsersService {
    UserModel registerUser(String login, String password, String email, String name ,String gender, String dob, String location, String phoneNumber);
    UserModel authentication(String login, String password);
    List<UserModel> getAllUsers();
    void deactivateUser(Integer id);
    void activateUser(Integer id);
    UserModel updateUser(String login, String dob, String gender, String phoneNumber, String location);
    public void deleteUserById(Integer userId);
    public UserModel findByIdWithFriendRequests(Long id);
    @Transactional
    public boolean sendFriendRequest(Integer id, UserModel sessionUser);

    @Transactional
    public List<UserModel> findRequestedFriends(UserModel sessionUser);

    @Transactional
    public List<UserModel> findFriendRequests(UserModel sessionUser);

    @Transactional(readOnly = true)
    public boolean deleteFriendRequest(Integer userId, Integer friendRequestId);
    public boolean acceptFriendRequest(Integer userId, Integer friendRequestId);
    public boolean declineFriendRequest(Integer userId, Integer friendRequestId);

    List<UserModel> findAllUsersExcludingSessionUser(UserModel sessionUserId);
    List<UserModel> findAllUsersStartingWithExcludingFriends(String prefix, Integer sessionUserId);

    @Transactional
    public void deleteUserByIdAndRemoveFromFriends(Integer userId);
   
}
