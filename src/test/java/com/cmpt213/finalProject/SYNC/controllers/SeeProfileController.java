package com.cmpt213.finalProject.SYNC.controllers;

import static org.mockito.Mockito.when;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
import com.cmpt213.finalProject.SYNC.service.UserDTO;
import com.cmpt213.finalProject.SYNC.service.UsersService;

@WebMvcTest(UsersController.class)
public class SeeProfileController {
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
    void testGetAllUsers() throws Exception {
        UserModel u1 = new UserModel();
        u1.setLogin("Spiderman");
        u1.setPassword(UserModel.hashFunc("1234"));
        u1.setEmail("spiderman@gmail.com");
        u1.setName("Peter Parker");

        UserModel u2 = new UserModel();
        u2.setLogin("Lepookie");
        u2.setPassword(UserModel.hashFunc("1234"));
        u2.setEmail("lebron@gmail.com");
        u2.setName("LeBron James");

        List<UserModel> users = new ArrayList<>();
        users.add(u1);
        users.add(u2);

        when(userRepository.findAll()).thenReturn(users);

        mockMvc.perform(MockMvcRequestBuilders.get("/users/view"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.view().name("showAll"))
            .andExpect(MockMvcResultMatchers.model().attributeExists("us"))
            .andExpect(MockMvcResultMatchers.model().attribute("us", users));
    }

    @Test
    void testGetLogin() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/login"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.view().name("login_page"));
    }

    @Test
    void testVerifyUser() throws Exception {
        String token = "verification-token";
        UserModel user = new UserModel();
        user.setToken(token);
        user.setEnabled(false);

        when(userRepository.findByToken(token)).thenReturn(Optional.of(user));

        mockMvc.perform(MockMvcRequestBuilders.get("/verify").param("code", token))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.view().name("verify_success"))
            .andExpect(MockMvcResultMatchers.model().attribute("message", "Your account has been successfully verified."));
    }

    @Test
    void testGetUsersStartingWith() throws Exception {
        UserModel sessionUser = new UserModel();
        sessionUser.setId(1);
        sessionUser.setLogin("SessionUser");

        UserModel u1 = new UserModel();
        u1.setLogin("Alice");

        UserModel u2 = new UserModel();
        u2.setLogin("Alex");

        List<UserDTO> users = List.of(new UserDTO(u1), new UserDTO(u2));

        when(userService.findAllUsersStartingWithExcludingFriends("Al", 1)).thenReturn(users);

        mockMvc.perform(MockMvcRequestBuilders.get("/getUsersStartingWith").param("prefix", "Al"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.content().contentType(APPLICATION_JSON_UTF8))
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].login").value("Alice"))
            .andExpect(MockMvcResultMatchers.jsonPath("$[1].login").value("Alex"));
    }
}
