package bupt.goodservice;

import bupt.goodservice.dto.JwtResponse;
import bupt.goodservice.dto.RegisterRequest;
import bupt.goodservice.model.ServiceRequest;
import bupt.goodservice.model.ServiceResponse;
import bupt.goodservice.model.User;
import bupt.goodservice.model.enums.ServiceResponseStatus;
import bupt.goodservice.service.ServiceRequestService;
import bupt.goodservice.service.ServiceResponseService;
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

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class StatsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private ServiceRequestService serviceRequestService;

    @Autowired
    private ServiceResponseService serviceResponseService;

    private String adminToken;
    private String userToken;
    private User requester;
    private User responder;

    @BeforeEach
    void setUp() throws Exception {
        adminToken = getAuthToken("admin", "admin123");

        // Create users
        requester = createUser("stats_requester", "44444444401");
        responder = createUser("stats_responder", "44444444402");
        userToken = getAuthToken("stats_requester", "password123");

        ServiceRequest req1 = createRequest(requester.getId(), "Request This Month 0");
        ServiceResponse res1 = createResponse(req1.getId(), responder.getId(), "Response This Month 0");
        serviceResponseService.acceptOrRejectResponse(res1.getId(), req1.getId(), ServiceResponseStatus.ACCEPTED, requester.getId());

        ServiceRequest req2 = createRequest(requester.getId(), "Request This Month 1");
        ServiceResponse res2 = createResponse(req2.getId(), responder.getId(), "Response This Month 1");
        serviceResponseService.acceptOrRejectResponse(res2.getId(), req2.getId(), ServiceResponseStatus.ACCEPTED, requester.getId());

        ServiceRequest req3 = createRequest(requester.getId(), "Request This Month 2");

    }

    private User createUser(String username, String phone) {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername(username);
        registerRequest.setPassword("password123");
        registerRequest.setName("User " + username);
        registerRequest.setPhone(phone);
        return userService.register(registerRequest);
    }

    private ServiceRequest createRequest(Long userId, String title) {
        ServiceRequest request = new ServiceRequest();
        request.setUserId(userId);
        request.setTitle(title);
        request.setRegionId(1L);
        return serviceRequestService.createServiceRequest(request);
    }

    private ServiceResponse createResponse(Long reqId, Long userId, String desc) {
        ServiceResponse response = new ServiceResponse();
        response.setRequestId(reqId);
        response.setUserId(userId);
        response.setDescription(desc);
        return serviceResponseService.createServiceResponse(response);
    }

    private String getAuthToken(String username, String password) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"" + username + "\", \"password\":\"" + password + "\"}"))
                .andReturn();
        return objectMapper.readValue(result.getResponse().getContentAsString(), JwtResponse.class).getToken();
    }

    @Test
    void getMonthlyStats_AsAdmin_Success() throws Exception {
        mockMvc.perform(get("/api/admin/stats/monthly")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void getMonthlyStats_AsUser_Forbidden() throws Exception {
        mockMvc.perform(get("/api/admin/stats/monthly")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void getMonthlyStats_FilterByDate_Success() throws Exception {
        String thisMonth = YearMonth.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));
        mockMvc.perform(get("/api/admin/stats/monthly")
                        .header("Authorization", "Bearer " + adminToken)
                        .param("startMonth", thisMonth)
                        .param("endMonth", thisMonth))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].month").value(thisMonth))
                .andExpect(jsonPath("$[0].requestCount").value(3));

        mockMvc.perform(get("/api/admin/stats/monthly")
                        .header("Authorization", "Bearer " + adminToken)
                        .param("startMonth", thisMonth)
                        .param("endMonth", thisMonth)
                        .param("success", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].month").value(thisMonth))
                .andExpect(jsonPath("$[0].requestCount").value(2));
    }
}

