package bupt.goodservice;

import bupt.goodservice.dto.JwtResponse;
import bupt.goodservice.dto.RegisterRequest;
import bupt.goodservice.model.ServiceRequest;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ServiceRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String userAToken;
    private String userBToken;
    private Long userAId;

    @BeforeEach
    void setUp() throws Exception {
        // Register and login User A
        userAId = registerAndLogin("userA_sr", "12345678901");
        userAToken = getAuthToken("userA_sr");

        // Register and login User B
        registerAndLogin("userB_sr", "12345678902");
        userBToken = getAuthToken("userB_sr");
    }

    private Long registerAndLogin(String username, String phone) throws Exception {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername(username);
        registerRequest.setPassword("password123");
        registerRequest.setName("Test " + username);
        registerRequest.setPhone(phone);
        
        MvcResult result = mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andReturn();
        // Extract user ID from response if needed, for simplicity we get it later or assume it
        return 1L; // Placeholder - in a real scenario, parse the ID from the response
    }

    private String getAuthToken(String username) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"" + username + "\", \"password\":\"password123\"}"))
                .andReturn();
        String responseString = result.getResponse().getContentAsString();
        JwtResponse jwtResponse = objectMapper.readValue(responseString, JwtResponse.class);
        return jwtResponse.getToken();
    }
    
    @Test
    void testCreateAndGetRequest() throws Exception {
        ServiceRequest newRequest = new ServiceRequest();
        newRequest.setTitle("Need plumbing services");
        newRequest.setDescription("Leaky faucet in the kitchen.");
        newRequest.setUserId(1L); // This should be set by the backend based on token

        MvcResult createResult = mockMvc.perform(post("/api/requests")
                        .header("Authorization", "Bearer " + userAToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andReturn();
        
        String responseString = createResult.getResponse().getContentAsString();
        ServiceRequest createdRequest = objectMapper.readValue(responseString, ServiceRequest.class);

        // Test Get by ID
        mockMvc.perform(get("/api/requests/" + createdRequest.getId())
                        .header("Authorization", "Bearer " + userAToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Need plumbing services"));
    }
    
    @Test
    void testUpdateRequest_Success() throws Exception {
        // User A creates a request
        ServiceRequest newRequest = new ServiceRequest();
        newRequest.setTitle("Initial Title");
        newRequest.setUserId(1L);
        MvcResult result = mockMvc.perform(post("/api/requests").header("Authorization", "Bearer " + userAToken).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(newRequest))).andReturn();
        ServiceRequest createdRequest = objectMapper.readValue(result.getResponse().getContentAsString(), ServiceRequest.class);

        // User A updates it
        createdRequest.setTitle("Updated Title");
        mockMvc.perform(put("/api/requests/" + createdRequest.getId())
                        .header("Authorization", "Bearer " + userAToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createdRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Title"));
    }

    @Test
    void testUpdateRequest_Forbidden() throws Exception {
        // User A creates a request
        ServiceRequest newRequest = new ServiceRequest();
        newRequest.setTitle("User A's Request");
        newRequest.setUserId(1L);
        MvcResult result = mockMvc.perform(post("/api/requests").header("Authorization", "Bearer " + userAToken).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(newRequest))).andReturn();
        ServiceRequest createdRequest = objectMapper.readValue(result.getResponse().getContentAsString(), ServiceRequest.class);

        // User B tries to update it
        createdRequest.setTitle("Malicious Update");
        mockMvc.perform(put("/api/requests/" + createdRequest.getId())
                        .header("Authorization", "Bearer " + userBToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createdRequest)))
                .andExpect(status().isInternalServerError()); // Expecting a security exception
    }
}
