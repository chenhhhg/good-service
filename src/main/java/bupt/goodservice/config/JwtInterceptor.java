package bupt.goodservice.config;

import bupt.goodservice.service.UserService;
import bupt.goodservice.utils.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.security.Principal;

@Component
public class JwtInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserService userService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            try {
                String username = jwtUtils.getUsernameFromToken(token);
                Long userId = jwtUtils.getUserIdFromToken(token);
                request.setAttribute("username", username);
                request.setAttribute("userId", userId);
                
                // For Principal object
                request.setAttribute("principal", (Principal) () -> username);
                
                return true;
            } catch (Exception e) {
                // Token is invalid
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token");
                return false;
            }
        }

        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Authorization header is missing or invalid");
        return false;
    }
}
