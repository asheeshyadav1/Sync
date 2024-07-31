package com.cmpt213.finalProject.SYNC.controllers;

import static org.mockito.Mockito.when;
import static org.hamcrest.Matchers.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.cmpt213.finalProject.SYNC.controller.UsersController;
import com.cmpt213.finalProject.SYNC.models.UserModel;
import com.cmpt213.finalProject.SYNC.repository.UserRepository;
import com.cmpt213.finalProject.SYNC.service.ImgurService;
import com.cmpt213.finalProject.SYNC.service.PostService;
import com.cmpt213.finalProject.SYNC.service.UsersService;
import com.cmpt213.finalProject.SYNC.service.friendDTO;

@WebMvcTest(UsersController.class)
public class SeeFriendsReqController {
    public static final MediaType APPLICATION_JSON_UTF8 = new MediaType(MediaType.APPLICATION_JSON.getType(), MediaType.APPLICATION_JSON.getSubtype(), StandardCharsets.UTF_8);

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private UsersService userService;

    @MockBean
    private PostService postService;

    @MockBean
    private ImgurService imgurService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testGetFriendRequests() throws Exception {
        // Mock user data
        UserModel u1 = new UserModel();
        u1.setLogin("Spiderman");
        u1.setPassword(UserModel.hashFunc("1234"));
        u1.setId(1);

        // Mock friendDTO data
        List<friendDTO> friendRequests = new ArrayList<>();
        friendRequests.add(new friendDTO("Lepookie", "lebron@gmail.com"));

        // Mock the service call to return the friendDTO list
        when(userService.findGotFriendRequests(u1)).thenReturn(friendRequests);

        // Test the controller method
        mockMvc.perform(MockMvcRequestBuilders.get("/getFriendRequests")
                .sessionAttr("session_user", u1))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.length()", is(friendRequests.size())))
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].login", is("Lepookie")));
    }

    @Test
    void testGetLogin() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/login"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.view().name("login_page"));
    }
}
