package bupt.goodservice.aspect;

import bupt.goodservice.mapper.ServiceRequestMapper;
import bupt.goodservice.mapper.ServiceResponseMapper;
import bupt.goodservice.model.ServiceRequest;
import bupt.goodservice.model.ServiceResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
public class OwnershipAspect {

    @Autowired
    private ServiceRequestMapper serviceRequestMapper;

    @Autowired
    private ServiceResponseMapper serviceResponseMapper;

    @Before("@annotation(checkOwnership)")
    public void checkOwnership(JoinPoint joinPoint, CheckOwnership checkOwnership) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        Long currentUserId = (Long) request.getAttribute("userId");

        if (currentUserId == null) {
            throw new SecurityException("User not authenticated.");
        }

        Long resourceId = (Long) joinPoint.getArgs()[0]; // 要修改的资源ID

        boolean isOwner = switch (checkOwnership.resourceType()) {
            case "ServiceRequest" -> {
                ServiceRequest serviceRequest = serviceRequestMapper.findById(resourceId);
                yield serviceRequest != null && serviceRequest.getUserId().equals(currentUserId);
            }
            case "ServiceResponse" -> {
                ServiceResponse serviceResponse = serviceResponseMapper.findById(resourceId);
                yield serviceResponse != null && serviceResponse.getUserId().equals(currentUserId);
            }
            default -> false;
        };

        if (!isOwner) {
            throw new SecurityException("User does not have permission to access this resource.");
        }
    }
}
