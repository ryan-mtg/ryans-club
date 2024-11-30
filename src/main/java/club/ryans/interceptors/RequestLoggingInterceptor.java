package club.ryans.interceptors;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;

@Component
@Slf4j
public class RequestLoggingInterceptor implements HandlerInterceptor {
    private final List<String> EXCLUDED_EXTENSIONS = Arrays.asList("ico", "png", "css", "js");

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        final String requestUri = request.getRequestURI();
        if (filter(requestUri)) {
            LOGGER.info("request: {} {}", request.getMethod(), requestUri);
        }
        return HandlerInterceptor.super.preHandle(request, response, handler);
    }

    private boolean filter(final String requestUri) {
        int dotIndex = requestUri.lastIndexOf('.');
        if (dotIndex < 0) {
            return true;
        }
        final String extension = requestUri.substring(dotIndex + 1);
        if (EXCLUDED_EXTENSIONS.contains(extension.toLowerCase())) {
            return false;
        }
        return true;
    }
}
