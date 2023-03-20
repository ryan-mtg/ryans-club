package club.ryans.security;

import club.ryans.discord.DiscordUserService;
import club.ryans.security.user.User;
import club.ryans.security.user.UserManager;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.RequestEntity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.endpoint.DefaultAuthorizationCodeTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequestEntityConverter;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@ControllerAdvice
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    private static final String USER_AGENT = "Ryan's Club (https://stfc.servobot.info/)";

    private final DiscordUserService discordUserService;
    private final UserManager userManager;

    @Override
    protected void configure(final HttpSecurity http) throws Exception {
        RequestCache requestCache = http.getSharedObject(RequestCache.class);

        if (requestCache == null) {
            requestCache = new HttpSessionRequestCache();
            http.setSharedObject(RequestCache.class, requestCache);
        }

        http.csrf().ignoringAntMatchers("/api/public/**")
            .and().authorizeRequests()
                .antMatchers("/login", "/user").authenticated()
                .antMatchers("/admin", "/admin/**", "/api/admin/**", "/scripts/admin.js") .access("hasRole('ADMIN')")
                .antMatchers("/register") .access("hasRole('UNREGISTERED')")
                .anyRequest().permitAll()
            .and().oauth2Login()
                .userInfoEndpoint().userService(discordUserService).and()
                .failureHandler(new LoginFailureHandler())
                .tokenEndpoint().accessTokenResponseClient(accessTokenResponseClient())
                .and()
                    .successHandler(new LoginSuccessHandler(requestCache))
            .and().logout(l -> l.logoutUrl("/logout").logoutSuccessUrl("/").permitAll());

        ;

        /*
        http.authorizeRequests().antMatchers("/", "/index.html", "/scripts/**", "/styles/**", "faveicon.ico", "/images/**").permitAll()
            .anyRequest().authenticated()
            .and().formLogin().loginPage("/login").permitAll()
            .and().logout() .permitAll();
         */
    }

    /*
    @Bean
    public AccessDecisionManager accessDecisionManager(
            final WebsiteSecurityExpressionHandler securityExpressionHandler) {
        WebExpressionVoter webExpressionVoter = new WebExpressionVoter();
        webExpressionVoter.setExpressionHandler(securityExpressionHandler);
        List<AccessDecisionVoter<?>> decisionVoters =
                Arrays.asList(webExpressionVoter, new RoleVoter(), new AuthenticatedVoter());
        return new AffirmativeBased(decisionVoters);
    }
     */

    private User lookupUser(final OAuth2AuthenticationToken oAuth2AuthenticationToken) {
        if (oAuth2AuthenticationToken != null && oAuth2AuthenticationToken.isAuthenticated()) {
            int userId = (int) oAuth2AuthenticationToken.getPrincipal().getAttributes().get(User.ID_PROPERTY);
            return userManager.lookupUser(userId);
        }

        return userManager.createUnauthenticatedUser();
    }

    @Bean
    public OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest> accessTokenResponseClient() {
        DefaultAuthorizationCodeTokenResponseClient client = new DefaultAuthorizationCodeTokenResponseClient();

        client.setRequestEntityConverter(new OAuth2AuthorizationCodeGrantRequestEntityConverter() {
            @Override
            public RequestEntity<?> convert(OAuth2AuthorizationCodeGrantRequest oauth2Request) {
                return addUserAgentHeader(super.convert(oauth2Request));
            }
        });

        return client;
    }

    @ModelAttribute
    private void addUser(final Model model, final OAuth2AuthenticationToken oAuth2AuthenticationToken) {
        model.addAttribute("user", lookupUser(oAuth2AuthenticationToken));
    }

    static RequestEntity<?> addUserAgentHeader(RequestEntity<?> request) {
        HttpHeaders headers = new HttpHeaders();
        headers.putAll(request.getHeaders());
        headers.add(HttpHeaders.USER_AGENT, USER_AGENT);

        return new RequestEntity<>(request.getBody(), headers, request.getMethod(), request.getUrl());
    }
}