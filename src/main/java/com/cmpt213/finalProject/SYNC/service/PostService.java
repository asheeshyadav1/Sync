package com.cmpt213.finalProject.SYNC.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cmpt213.finalProject.SYNC.models.UserModel;
import com.cmpt213.finalProject.SYNC.models.UserPost;
import com.cmpt213.finalProject.SYNC.repository.UserPostRepository;
import com.cmpt213.finalProject.SYNC.repository.UserRepository;

@Service
public class PostService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ImgurService imgurService;

    @Autowired
    private UserPostRepository userPostRepository;

    public UserPost addPost(Integer userId, String caption, MultipartFile image) {
        UserModel user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        String imageUrl = imgurService.uploadImage(image);
        UserPost post = new UserPost(imageUrl, caption, user.getId());
        user.getUserPosts().add(post);
        userRepository.save(user);

        return post;
    }

    public List<UserPost> getUserPosts(Integer userId) {
        UserModel user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        return user.getUserPosts();
    }

    public List<UserPost> getRecentFriendPosts(Integer userId) {
        UserModel user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        List<Integer> friendIds = user.getFriends().stream().map(f -> f.getFriendId()).collect(Collectors.toList());

        return friendIds.stream().map(id -> {
            UserModel friend = userRepository.findById(id).orElse(null);
            if (friend == null) {
                System.out.println("Friend not found for ID: " + id);
            }
            return friend;
        }).filter(friend -> friend != null).flatMap(friend -> {
            List<UserPost> posts = friend.getUserPosts();
            return posts.stream();
        }).sorted((p1, p2) -> p2.getPublishTime().compareTo(p1.getPublishTime())).collect(Collectors.toList());
    }

    public UserPost findPostById(Integer postId) {
        return userPostRepository.findById(postId).orElseThrow(() -> new RuntimeException("Post not found"));
    }

    public void savePost(UserPost post) {
        userPostRepository.save(post);
    }

    public void likePost(Integer userId, Integer postId) {
        UserPost post = findPostById(postId);
        System.out.println("Before liking: " + post.getLikes().size());
        post.addLike(userId);
        savePost(post);
        System.out.println("After liking: " + post.getLikes().size());
    }

    public void dislikePost(Integer userId, Integer postId) {
        UserPost post = findPostById(postId);
        System.out.println("Before disliking: " + post.getDislikes().size());
        post.addDislike(userId);
        savePost(post);
        System.out.println("After disliking: " + post.getDislikes().size());
    }

}
