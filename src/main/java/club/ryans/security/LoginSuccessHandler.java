package club.ryans.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class LoginSuccessHandler implements AuthenticationSuccessHandler {
    private static final int SECONDS_IN_A_DAY = 24 * 60 * 60;
    private static final int SESSION_TIMEOUT = 3 * SECONDS_IN_A_DAY;

    private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
    private final RequestCache requestCache;

    private static enum UserLevel {
        ADMIN,
        REGISTERED_USER,
        UNREGISTERED_USER,
        UNAUTHENTICATED_USER
    }

    public LoginSuccessHandler(final RequestCache requestCache) {
        this.requestCache = requestCache;
    }

    @Override
    public void onAuthenticationSuccess(final HttpServletRequest request, final HttpServletResponse response,
            final Authentication authentication) throws IOException, ServletException {

        handle(request, response, authentication);
        clearAuthenticationAttributes(request);
    }

    private void handle(final HttpServletRequest request, final HttpServletResponse response,
            final Authentication authentication) throws IOException {

        request.getSession().setMaxInactiveInterval(SESSION_TIMEOUT);

        if (response.isCommitted()) {
            return;
        }

        UserLevel userLevel = determineUserLevel(authentication);
        String targetUrl = determineRedirectUrl(userLevel, request, response);

        if (request.getContextPath().equals(targetUrl)) {
            return;
        }
        redirectStrategy.sendRedirect(request, response, determineRedirectUrl(userLevel, request, response));
    }

    private String determineRedirectUrl(final UserLevel userLevel, final HttpServletRequest request,
            final HttpServletResponse response) {
        if (userLevel == UserLevel.UNREGISTERED_USER) {
            return getUserLevelUrl(userLevel);
        }

        SavedRequest savedRequest = requestCache.getRequest(request, response);
        if (savedRequest == null) {
            return getUserLevelUrl(userLevel);
        }

        this.requestCache.removeRequest(request, response);

        if (!savedRequest.getRedirectUrl().equals("/")) {
            return savedRequest.getRedirectUrl();
        }

        return getUserLevelUrl(userLevel);
    }

    private UserLevel determineUserLevel(final Authentication authentication) {
        for (GrantedAuthority authority : authentication.getAuthorities()) {
            if (authority.getAuthority().equals("ROLE_ADMIN")) {
                return UserLevel.ADMIN;
            }

            if (authority.getAuthority().equals("ROLE_UNREGISTERED")) {
                return UserLevel.UNREGISTERED_USER;
            }

            if (authority.getAuthority().equals("ROLE_REGISTERED")) {
                return UserLevel.REGISTERED_USER;
            }
        }
        return UserLevel.UNAUTHENTICATED_USER;
    }

    private String getUserLevelUrl(final UserLevel userLevel) {
        switch (userLevel) {
            case REGISTERED_USER:
                return "/user";
            case ADMIN:
                return "/admin";
            case UNREGISTERED_USER:
                return "/register";
            case UNAUTHENTICATED_USER:
                return "/login";
        }
        return "/";
    }

    protected void clearAuthenticationAttributes(final HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return;
        }
        session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
    }
}
