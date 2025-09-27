package bupt.goodservice;

import bupt.goodservice.dto.JwtResponse;
import bupt.goodservice.dto.RegisterRequest;
import bupt.goodservice.dto.UserUpdateRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String authToken;

    @BeforeEach
    void setUp() throws Exception {
        // Register and login a user to get a token for authenticated requests
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername("testuser_uc");
        registerRequest.setPassword("password123");
        registerRequest.setName("Test User");
        registerRequest.setPhone("11122233344");
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)));

        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"testuser_uc\", \"password\":\"password123\"}"))
                .andReturn();
        String responseString = result.getResponse().getContentAsString();
        JwtResponse jwtResponse = objectMapper.readValue(responseString, JwtResponse.class);
        authToken = jwtResponse.getToken();
    }

    @Test
    void testGetCurrentUser_Success() throws Exception {
        mockMvc.perform(get("/api/users/me")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser_uc"));
    }

    @Test
    void testGetCurrentUser_Unauthorized() throws Exception {
        mockMvc.perform(get("/api/users/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testUpdateUser_Success() throws Exception {
        UserUpdateRequest updateRequest = new UserUpdateRequest();
        updateRequest.setPhone("55566677788");
        updateRequest.setProfile("New profile info");

        mockMvc.perform(put("/api/users/me")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.phone").value("55566677788"))
                .andExpect(jsonPath("$.profile").value("New profile info"));
    }
}
