package bupt.goodservice.aspect;

import bupt.goodservice.model.enums.UserType;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.IOException;

@Aspect
@Component
@Slf4j
public class AdminAspect {
    @Before("@annotation(checkIfAdmin)")
    public void checkIfAdmin(JoinPoint joinPoint, CheckIfAdmin checkIfAdmin) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getResponse();
        Long userType = (Long) request.getAttribute("userType");
        try {
            if (userType == null) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "User type missed.");
                throw new SecurityException("User type missed.");
            }
            if (userType != UserType.ADMIN.getValue()) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "User does not have permission to access this resource.");
                throw new SecurityException("User does not have permission to access this resource.");
            }
        } catch (IOException e) {
            log.error("Response failed! request: {}", request);
        }
    }
}
