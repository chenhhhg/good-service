package bupt.goodservice;

import bupt.goodservice.dto.RegisterRequest;
import bupt.goodservice.dto.ServiceResponses;
import bupt.goodservice.mapper.RegionalDivisionsMapper;
import bupt.goodservice.mapper.ServiceRequestMapper;
import bupt.goodservice.mapper.ServiceResponseMapper;
import bupt.goodservice.mapper.UserMapper;
import bupt.goodservice.model.ServiceRequest;
import bupt.goodservice.model.ServiceResponse;
import bupt.goodservice.model.User;
import bupt.goodservice.model.enums.ServiceRequestStatus;
import bupt.goodservice.model.enums.ServiceResponseStatus;
import bupt.goodservice.service.FileStorageService;
import bupt.goodservice.service.ServiceRequestService;
import bupt.goodservice.service.ServiceResponseService;
import bupt.goodservice.service.UserService;
import com.github.javafaker.Faker;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

@SpringBootTest
@Slf4j
public class DataCreateTest {
    // 一些免费的随机图片API
    private static final String[] IMAGE_APIS = {
            "https://picsum.photos/800/600",          // 随机图片
            "https://picsum.photos/800/600?random=1", // 随机图片带参数
            "https://source.unsplash.com/random/800x600", // Unsplash随机图片
            "https://loremflickr.com/800/600",        // 随机图片
            "https://placekitten.com/800/600",        // 随机猫咪图片
            "https://baconmockup.com/800/600",        // 培根图片
            "https://placeimg.com/800/600/any"        // 随机图片
    };
    private final Random random = new Random(System.currentTimeMillis());
    private final Faker faker = new Faker(Locale.CHINA, random);
    @Autowired
    UserService userService;
    @Autowired
    ServiceRequestService requestService;
    @Autowired
    ServiceResponseService responseService;
    @Autowired
    FileStorageService fileStorageService;
    @Autowired
    UserMapper userMapper;
    @Autowired
    ServiceRequestMapper requestMapper;
    @Autowired
    ServiceResponseMapper responseMapper;
    @Autowired
    RegionalDivisionsMapper regionalDivisionsMapper;
    @Value("${test.create-data:false}")
    private Boolean create;
    @Value("${test.user.count:10}")
    private Integer userCount;
    @Value("${test.request.count:10}")
    private Integer requestCount;
    @Value("${test.response.count:10}")
    private Integer responseCount;
    private List<Long> users;
    private List<Long> requests;
    private List<Long> responses;
    private List<Long> regions;

    /**
     * 从随机图片API下载图片并转换为MultipartFile
     */
    public static MultipartFile downloadRandomImage() {
        Random random = new Random();
        String imageUrl = IMAGE_APIS[random.nextInt(IMAGE_APIS.length)];

        try {
            return downloadImageFromUrl(imageUrl, "random-image.jpg");
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * 从指定URL下载图片并转换为MultipartFile
     */
    public static MultipartFile downloadImageFromUrl(String imageUrl, String filename) throws IOException {
        URL url = new URL(imageUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(10000);
        connection.setReadTimeout(10000);

        // 设置User-Agent，避免被某些网站拒绝
        connection.setRequestProperty("User-Agent",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36");

        try (InputStream inputStream = connection.getInputStream()) {
            byte[] imageBytes = inputStream.readAllBytes();

            // 根据URL确定文件类型
            String contentType = connection.getContentType();
            if (contentType == null) {
                contentType = "image/jpeg"; // 默认类型
            }

            return new MockMultipartFile(
                    "file",           // 参数名
                    filename,         // 文件名
                    contentType,      // 内容类型
                    imageBytes        // 文件内容
            ) {
            };
        } finally {
            connection.disconnect();
        }
    }

    @Test
    public void createData() throws Exception {
        if (!create) {
            return;
        }
        regions = regionalDivisionsMapper.selectAll();
        createUsers(userCount);
        createRequests(requestCount);
        createResponses(responseCount);
//        cancelRequests(requestCount >> 2);
//        cancelReposes(responseCount >> 2);
//        acceptOrReject(requestCount >> 2);
    }

    private void acceptOrReject(int count) {
        for (Long requestId : requests) {
            if (count == 0) {
                break;
            }
            ServiceRequest byId = requestService.getServiceRequestById(requestId);
            if (!byId.getStatus().equals(ServiceRequestStatus.PUBLISHED)) {
                continue;
            }
            ServiceResponses responses = responseService.getServiceResponsesByRequestId(requestId, 1, 1000);
            if (responses.getData().isEmpty()) {
                continue;
            }
            long responseId = -1;
            for (ServiceResponse response : responses.getData()) {
                if (response.getStatus().equals(ServiceResponseStatus.PENDING)) {
                    responseId = response.getId();
                    break;
                }
            }
            if (responseId == -1) {
                continue;
            }
            count--;
            responseService.acceptOrRejectResponse(responseId, requestId,
                    random.nextBoolean() ? ServiceResponseStatus.ACCEPTED : ServiceResponseStatus.REJECTED,
                    byId.getUserId());
        }
    }

    private void cancelReposes(int count) {
        for (int i = 0; i < count; i++) {
            responseService.deleteServiceResponse(responses.get(random.nextInt(responses.size())));
        }
    }

    private void cancelRequests(int count) {
        for (int i = 0; i < count; i++) {
            requestService.deleteServiceRequest(requests.get(random.nextInt(requests.size())));
        }
    }

    private void createResponses(Integer count) {
        List<Long> list = responseMapper.selectAll();
        if (list.size() > count) {
            return;
        }
        int diff = count - list.size();
        List<ServiceResponse> responses = new ArrayList<>();
        for (int i = 0; i < diff; i++) {
            ServiceResponse serviceResponse = new ServiceResponse();
            serviceResponse.setUserId(users.get(random.nextInt(users.size())));
            serviceResponse.setRequestId(this.requests.get(random.nextInt(this.requests.size())));

            serviceResponse.setDescription(faker.backToTheFuture().quote());

            if (random.nextInt(2) < 1) {
                MultipartFile file = downloadRandomImage();
                if (file != null) {
                    String fileName = fileStorageService.storeFile(file);
                    serviceResponse.setImageFiles(fileName);
                }
            }

            responses.add(serviceResponse);
        }

        for (ServiceResponse response : responses) {
            try {
                responseService.createServiceResponse(response);
                list.add(response.getId());
            } catch (Exception e) {
                log.error("create failed: {}", response);
            }
        }
        this.responses = list;
    }

    private void createRequests(int count) {
        List<Long> list = requestMapper.selectAll();
        if (list.size() > count) {
            return;
        }
        int diff = count - list.size();
        List<ServiceRequest> requests = new ArrayList<>();
        for (int i = 0; i < diff; i++) {
            ServiceRequest serviceRequest = new ServiceRequest();
            serviceRequest.setUserId(users.get(random.nextInt(users.size())));
            serviceRequest.setRegionId(regions.get(random.nextInt(regions.size())));

            serviceRequest.setTitle(generateTitle(faker.company().name(), faker.job().title(),
                    faker.animal().name(), random.nextInt(7)));
            serviceRequest.setDescription(faker.rickAndMorty().quote());

            if (random.nextInt(2) < 1) {
                MultipartFile file = downloadRandomImage();
                if (file != null) {
                    String fileName = fileStorageService.storeFile(file);
                    serviceRequest.setImageFiles(fileName);
                }
            }

            requests.add(serviceRequest);
        }
        for (ServiceRequest request : requests) {
            try {
                requestService.createServiceRequest(request);
                list.add(request.getId());
            } catch (Exception e) {
                log.error("create failed: {}", request);
            }
        }
        this.requests = list;
    }

    private String generateTitle(String name, String title, String username, int type) {
        String suffix = switch (type) {
            case 0 -> "打败" + faker.dragonBall().character();
            case 1 -> "跳槽" + faker.company().name();
            case 2 -> "成为" + faker.job().position();
            case 3 -> "开发" + faker.app().name();
            case 4 -> "去到" + faker.address().cityName();
            case 5 -> "买到" + faker.beer().name();
            case 6 -> "加入" + faker.elderScrolls().region();
            default -> "学会" + faker.job().keySkills();
        };
        return "在" + name + "从事" + title + "的" + username + "想知道如何" + suffix;
    }

    private void createUsers(int count) {
        List<Long> list = userMapper.selectAll();
        if (list.size() > count) {
            return;
        }
        int diff = count - list.size();
        List<RegisterRequest> requests = new ArrayList<>();
        for (int i = 0; i < diff; i++) {
            RegisterRequest request = new RegisterRequest();
            String[] split = faker.name().username().split("\\.");
            request.setName(split[1] + split[0]);
            String password = faker.cat().name() + faker.number().numberBetween(0, 100000);
            request.setPassword(password);
            request.setUsername(password);
            request.setPhone(faker.phoneNumber().phoneNumber());
            requests.add(request);
        }
        for (RegisterRequest request : requests) {
            try {
                User user = userService.register(request);
                list.add(user.getId());
            } catch (Exception e) {
                log.error("register failed: {}", request);
            }
        }
        users = list;
    }
}
