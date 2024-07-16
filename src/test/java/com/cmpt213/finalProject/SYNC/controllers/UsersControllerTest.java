package com.cmpt213.finalProject.SYNC.controllers;

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
import static org.mockito.Mockito.when;
import org.hamcrest.Matchers;

import static org.hamcrest.Matchers.*;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;


@WebMvcTest(UsersController.class)
public class UsersControllerTest {
    public static final MediaType APPLICATION_JSON_UTF8 = new MediaType(MediaType.APPLICATION_JSON.getType(), MediaType.APPLICATION_JSON.getSubtype(), Charset.forName("utf8"));
    
    @MockBean
    private UserRepository userRepository;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testGetAllUsers() throws Exception{
        UserModel u1 = new UserModel();
        u1.setLogin("Spiderman");
        u1.setPassword("1234");
        

        UserModel u2 = new UserModel();
        u2.setLogin("Lepookie");
        u2.setPassword("1234");
        
        List<UserModel> users = new ArrayList<>();
        users.add(u1);
        users.add(u2);

        when(userRepository.findAll()).thenReturn(users);
        mockMvc.perform(MockMvcRequestBuilders.get("/users/view"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.view().name("showAll"))
            
            .andExpect(MockMvcResultMatchers.model().attribute("us", hasItem(
                allOf(
                    hasProperty("login", Matchers.is("Spiderman")),
                    hasProperty("password", Matchers.is("1234"))
                    
                )
            )));
    }
    @Test
    void testGetLogin() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/login"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.view().name("login"));
    } 


}
