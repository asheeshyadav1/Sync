package com.cmpt213.finalProject.SYNC.service;

import com.cmpt213.finalProject.SYNC.models.UserModel;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public interface UsersService {
    UserModel registerUser(String login, String password, String email, String name ,String gender, String dob, String location, String phoneNumber);
    UserModel authentication(String login, String password);
    List<UserModel> getAllUsers();
    void deactivateUser(Integer id);
    void activateUser(Integer id);
    UserModel updateUser(String login, String dob, String gender, String phoneNumber, String location);
    public String updateProfilePicture(String login, MultipartFile image);
    public void saveUser(UserModel user);
    // public void deleteUserById(Integer userId);
    public UserModel findByIdWithFriendRequests(Long id);
    @Transactional
    public boolean sendFriendRequest(Integer id, UserModel sessionUser);

    @Transactional(readOnly = true)
    public List<Integer> findRequestedFriendIds(UserModel sessionUser);

    @Transactional(readOnly = true)
    public boolean deleteFriendRequest(Integer userId, Integer friendRequestId);
    @Transactional
    public void deleteUserByIdAndRemoveFromFriends(Integer userId);

    @Transactional(readOnly = true)
    public List<UserModel> findAllUsersStartingWithExcludingFriends(String prefix, Integer sessionUserId);

    @Transactional(readOnly = true)
    public List<UserModel> findAllUsersExcludingSessionUser(Integer sessionUserId);

    @Transactional
    public boolean declineFriendRequest(Integer userId, Integer friendRequestId);
    @Transactional
    public boolean acceptFriendRequest(Integer userId, Integer friendRequestId);

    @Transactional(readOnly = true)
    public List<UserModel> findGotFriendRequests(UserModel sessionUser);
}
