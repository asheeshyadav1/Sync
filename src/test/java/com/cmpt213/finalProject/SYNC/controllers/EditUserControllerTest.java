package com.cmpt213.finalProject.SYNC.controllers;

import static org.mockito.Mockito.when;

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

@WebMvcTest(UsersController.class)
public class EditUserControllerTest {
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
    void testGetLogin() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/login"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.view().name("login_page"));
    }

    @Test
    void testEditUser() throws Exception {
        UserModel existingUser = new UserModel();
        existingUser.setId(1);
        existingUser.setLogin("Spiderman");
        existingUser.setPassword(UserModel.hashFunc("1234"));
        existingUser.setEmail("spiderman@example.com");
        existingUser.setName("Peter Parker");
        existingUser.setGender("Male");
        existingUser.setDob("2000-05-03");
        existingUser.setLocation("New York");
        existingUser.setPhoneNumber("1234567890");

        UserModel updatedUser = new UserModel();
        updatedUser.setId(1);
        updatedUser.setLogin("Spiderman");
        updatedUser.setPassword(UserModel.hashFunc("1234"));
        updatedUser.setEmail("spiderman@example.com");
        updatedUser.setName("Peter B. Parker");
        updatedUser.setGender("Male");
        updatedUser.setDob("2000-05-03");
        updatedUser.setLocation("New York");
        updatedUser.setPhoneNumber("1234567890");

        when(userService.updateUser(
            "Spiderman", "2000-05-03", "Male", "1234567890", "New York", 0.0, 0.0
        )).thenReturn(updatedUser);

        mockMvc.perform(MockMvcRequestBuilders.post("/editUser")
                .param("login", "Spiderman")
                .param("dob", "2000-05-03")
                .param("gender", "Male")
                .param("phoneNumber", "1234567890")
                .param("location", "New York")
                .param("profilePictureFile", "") // Assuming no file upload
                .param("resetProfilePicture", "false"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.view().name("viewProfile"))
            .andExpect(MockMvcResultMatchers.model().attribute("user", updatedUser));
    }
}
