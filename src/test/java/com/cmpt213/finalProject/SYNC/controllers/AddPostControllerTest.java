package com.cmpt213.finalProject.SYNC.controllers;
import com.cmpt213.finalProject.SYNC.controllers.AddPostController;
import com.cmpt213.finalProject.SYNC.models.UserModel;
import com.cmpt213.finalProject.SYNC.models.UserPost;
import com.cmpt213.finalProject.SYNC.services.PostService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.Mockito.*;

@SpringBootTest
@AutoConfigureMockMvc
public class AddPostControllerTest {

    private static final int userId = 42;
    private static final String urlPath = "/addPost";

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private PostService postService;

    @InjectMocks
    private AddPostController addPostController;

    private MockHttpSession mockHttpSession;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(addPostController).build();

        // Mock HttpSession and session_user attribute
        mockHttpSession = new MockHttpSession();
        UserModel sessionUser = new UserModel();
        sessionUser.setId(userId); // Example user ID
        mockHttpSession.setAttribute("session_user", sessionUser);
    }

    @Test
    public void testAddPostWithImage() throws Exception {
        // Mock MultipartFile
        final InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("test.png");
        final MockMultipartFile mockImage = new MockMultipartFile("image", "test.png", "image/png", inputStream);

        // Mock behavior of postService.addPost()
        UserPost mockPost = new UserPost("http://example.com/test.png", "Test caption", userId); // Example post
        when(PostService.addPost(any(Integer.class), any(String.class), any(MultipartFile.class))).thenReturn(mockPost);

        // Perform POST request to /addPost
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.multipart(urlPath)
                .file(mockImage)
                .param("caption", "Test caption")
                .session(mockHttpSession))
                .andExpect(status().is3xxRedirection())
                .andReturn();

        // Verify postService.addPost() was called once
        verify(postService, times(1)).addPost(any(Integer.class), any(String.class), any(MultipartFile.class));
    }
    }