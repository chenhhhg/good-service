package bupt.goodservice;

import bupt.goodservice.dto.JwtResponse;
import bupt.goodservice.dto.RegisterRequest;
import bupt.goodservice.model.ServiceRequest;
import bupt.goodservice.model.ServiceResponse;
import bupt.goodservice.model.User;
import bupt.goodservice.model.enums.ServiceResponseStatus;
import bupt.goodservice.service.ServiceRequestService;
import bupt.goodservice.service.UserService;
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
public class ServiceResponseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private ServiceRequestService serviceRequestService;

    private String requesterToken;
    private String responderToken;
    private Long requesterId;
    private Long responderId;
    private ServiceRequest testServiceRequest;

    @BeforeEach
    void setUp() throws Exception {
        // Register requester and responder
        requesterId = registerUserAndGetId("requester_srs", "11111111101");
        responderId = registerUserAndGetId("responder_srs", "11111111102");

        requesterToken = getAuthToken("requester_srs");
        responderToken = getAuthToken("responder_srs");

        // Requester creates a service request
        ServiceRequest newRequest = new ServiceRequest();
        newRequest.setTitle("Need help with coding");
        newRequest.setDescription("A Java Spring Boot project.");
        newRequest.setUserId(requesterId);
        testServiceRequest = serviceRequestService.createServiceRequest(newRequest);
    }

    private Long registerUserAndGetId(String username, String phone) throws Exception {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername(username);
        registerRequest.setPassword("password123");
        registerRequest.setName("Test " + username);
        registerRequest.setPhone(phone);

        MvcResult result = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk())
                .andReturn();
        User user = objectMapper.readValue(result.getResponse().getContentAsString(), User.class);
        return user.getId();
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
    void testCreateAndGetResponse() throws Exception {
        ServiceResponse newResponse = new ServiceResponse();
        newResponse.setRequestId(testServiceRequest.getId());
        newResponse.setUserId(responderId);
        newResponse.setDescription("I can help with that!");

        MvcResult createResult = mockMvc.perform(post("/api/responses")
                        .header("Authorization", "Bearer " + responderToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newResponse)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andReturn();
        ServiceResponse createdResponse = objectMapper.readValue(createResult.getResponse().getContentAsString(), ServiceResponse.class);

        // Test Get by ID
        mockMvc.perform(get("/api/responses/" + createdResponse.getId())
                        .header("Authorization", "Bearer " + responderToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value("I can help with that!"));
    }

    @Test
    void testUpdateResponse_Success() throws Exception {
        // Responder creates a response
        ServiceResponse newResponse = new ServiceResponse();
        newResponse.setRequestId(testServiceRequest.getId());
        newResponse.setUserId(responderId);
        newResponse.setDescription("Initial response");
        MvcResult result = mockMvc.perform(post("/api/responses").header("Authorization", "Bearer " + responderToken).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(newResponse))).andReturn();
        ServiceResponse createdResponse = objectMapper.readValue(result.getResponse().getContentAsString(), ServiceResponse.class);

        // Responder updates it
        createdResponse.setDescription("Updated response");
        mockMvc.perform(put("/api/responses/" + createdResponse.getId())
                        .header("Authorization", "Bearer " + responderToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createdResponse)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value("Updated response"));
    }

    @Test
    void testUpdateResponse_Forbidden() throws Exception {
        // Responder creates a response
        ServiceResponse newResponse = new ServiceResponse();
        newResponse.setRequestId(testServiceRequest.getId());
        newResponse.setUserId(responderId);
        newResponse.setDescription("A response");
        MvcResult result = mockMvc.perform(post("/api/responses").header("Authorization", "Bearer " + responderToken).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(newResponse))).andReturn();
        ServiceResponse createdResponse = objectMapper.readValue(result.getResponse().getContentAsString(), ServiceResponse.class);

        // Requester (another user) tries to update it
        createdResponse.setDescription("Malicious update");
        mockMvc.perform(put("/api/responses/" + createdResponse.getId())
                        .header("Authorization", "Bearer " + requesterToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createdResponse)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void testDeleteResponse_Success() throws Exception {
        // Responder creates a response
        ServiceResponse newResponse = new ServiceResponse();
        newResponse.setRequestId(testServiceRequest.getId());
        newResponse.setUserId(responderId);
        newResponse.setDescription("To be deleted");
        MvcResult result = mockMvc.perform(post("/api/responses").header("Authorization", "Bearer " + responderToken).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(newResponse))).andReturn();
        ServiceResponse createdResponse = objectMapper.readValue(result.getResponse().getContentAsString(), ServiceResponse.class);

        // Responder deletes it
        mockMvc.perform(delete("/api/responses/" + createdResponse.getId())
                        .header("Authorization", "Bearer " + responderToken))
                .andExpect(status().isNoContent());
    }

    @Test
    void testUpdateStatus_RequesterAccepts_Success() throws Exception {
        // Responder creates a response
        ServiceResponse newResponse = new ServiceResponse();
        newResponse.setRequestId(testServiceRequest.getId());
        newResponse.setUserId(responderId);
        newResponse.setDescription("Please accept my help");
        MvcResult result = mockMvc.perform(post("/api/responses").header("Authorization", "Bearer " + responderToken).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(newResponse))).andReturn();
        ServiceResponse createdResponse = objectMapper.readValue(result.getResponse().getContentAsString(), ServiceResponse.class);

        // Requester of the original request accepts the response
        mockMvc.perform(patch("/api/responses/" + createdResponse.getId() + "/status")
                        .param("ts", "1")
                        .param("rId", String.valueOf(createdResponse.getRequestId()))
                        .header("Authorization", "Bearer " + requesterToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(ServiceResponseStatus.ACCEPTED.getValue()));
    }

    @Test
    void testUpdateStatus_ResponderOrOtherUser_Forbidden() throws Exception {
        // Responder creates a response
        ServiceResponse newResponse = new ServiceResponse();
        newResponse.setRequestId(testServiceRequest.getId());
        newResponse.setUserId(responderId);
        newResponse.setDescription("Trying to self-accept");
        MvcResult result = mockMvc.perform(post("/api/responses").header("Authorization", "Bearer " + responderToken).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(newResponse))).andReturn();
        ServiceResponse createdResponse = objectMapper.readValue(result.getResponse().getContentAsString(), ServiceResponse.class);

        // Responder tries to accept their own response, which should be forbidden
        mockMvc.perform(patch("/api/responses/" + createdResponse.getId() + "/status")
                        .param("ts", "1")
                        .param("rId", String.valueOf(createdResponse.getRequestId()))
                        .header("Authorization", "Bearer " + responderToken))
                .andExpect(status().isForbidden());
    }
}
