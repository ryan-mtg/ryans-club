package club.ryans.interceptors;

import club.ryans.models.managers.UserManager;
import club.ryans.models.player.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
@Slf4j
public class CommunityModInterceptor implements HandlerInterceptor {

    @Autowired
    private UserManager userManager;

    @Override
    public boolean preHandle(final HttpServletRequest request, final HttpServletResponse response, final Object handler)
            throws Exception {
        HandlerMethod handlerMethod = (HandlerMethod) handler;

        CommunityModEntrypoint communityModEntrypoint = handlerMethod.getMethodAnnotation(CommunityModEntrypoint.class);
        if (communityModEntrypoint == null) {
            LOGGER.info("CMI unable to get method");
            return true;
        }

        final String tokenName = communityModEntrypoint.token();
        final String token = request.getHeader(tokenName);
        LOGGER.info("CMI handling {} with tokenName/token: {}/{}", request.getRequestURI(), tokenName, token);

        if (token == null) {
            LOGGER.info("Missing sync-token");
            return handleUnauthorizedAccess(response);
        }

        User user = userManager.lookupUserBySyncToken(token);
        if (user == null) {
            LOGGER.info("Unauthorized sync-token: {}", token);
            return handleUnauthorizedAccess(response);
        }

        LOGGER.info("Token belongs to: {}", user.getDisplayName());
        request.setAttribute("sync-user", user);
        return true;
    }

    @Override
    public void postHandle(final HttpServletRequest request, final HttpServletResponse response, final Object handler,
        @Nullable final ModelAndView modelAndView) throws Exception {
    }

    @Override
    public void afterCompletion(final HttpServletRequest request, final HttpServletResponse response,
        final Object handler, @Nullable final Exception ex) throws Exception {
    }

    private boolean handleUnauthorizedAccess(final HttpServletResponse response) {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        return false;
    }
}