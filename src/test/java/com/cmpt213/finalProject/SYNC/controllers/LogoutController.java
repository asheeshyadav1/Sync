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
public class LogoutController {
    public static final MediaType APPLICATION_JSON_UTF8 = new MediaType(MediaType.APPLICATION_JSON.getType(), MediaType.APPLICATION_JSON.getSubtype(), StandardCharsets.UTF_8);

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private UsersService userService;

    //@MockBean
    //private UserModel userModel;

    @MockBean
    private PostService postService;

    @MockBean
    private ImgurService imgurService;
    

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testMaps() throws Exception {


        UserModel u1 = new UserModel();
        u1.setLogin("Spiderman");
        String hashedPassword = UserModel.hashFunc("1234");
        u1.setPassword(hashedPassword);
       
        //UserModel u3 = userService.registerUser("Spiderman", hashedPassword,"y@gmail.com", "Asheesh","Male", "1999-01-01", "Vancouver", "1234567890");


        UserModel u2 = new UserModel();
        u2.setLogin("Lepookie");
        u2.setPassword(hashedPassword);
        u2.setEmail("lebron@gmail.com");
        u2.setName("goat");
        u2.setGender("male");
        u2.setDob("1999-01-01");
        u2.setLocation("Vancouver");
        u2.setPhoneNumber("1234567890");


        List<UserModel> users = new ArrayList<>();
        users.add(u1);
        users.add(u2);

        when(userRepository.findAll()).thenReturn(users);

        //Put the testing with the backend here
       
    }
    
    @Test
    void testGetLogin() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/login"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.view().name("login_page"));
    }

}
