package bupt.goodservice;

import bupt.goodservice.dto.JwtResponse;
import bupt.goodservice.dto.RegisterRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class FileUploadControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String userToken;

    @BeforeEach
    void setUp() throws Exception {
        // Register a user
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername("file_uploader");
        registerRequest.setPassword("password123");
        registerRequest.setName("File Uploader");
        registerRequest.setPhone("33333333333");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)));

        // Login to get token
        userToken = getAuthToken("file_uploader");
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
    void testUploadAndDownloadFile() throws Exception {
        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "hello.txt",
                MediaType.TEXT_PLAIN_VALUE,
                "Hello, World!".getBytes()
        );

        MvcResult uploadResult = mockMvc.perform(multipart("/api/files/upload")
                        .file(mockFile)
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fileName").exists())
                .andExpect(jsonPath("$.fileDownloadUri").exists())
                .andReturn();

        String responseString = uploadResult.getResponse().getContentAsString();
        String fileName = objectMapper.readTree(responseString).get("fileName").asText();

        mockMvc.perform(get("/api/files/download/" + fileName)
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", "attachment; filename=\"" + fileName + "\""));
    }

    @Test
    void testUploadFile_Unauthorized() throws Exception {
        MockMultipartFile mockFile = new MockMultipartFile(
                "file", "unauthorized.txt", MediaType.TEXT_PLAIN_VALUE, "test".getBytes());

        mockMvc.perform(multipart("/api/files/upload").file(mockFile))
                .andExpect(status().isUnauthorized());
    }
}
