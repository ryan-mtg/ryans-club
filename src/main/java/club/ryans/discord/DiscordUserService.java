package club.ryans.discord;

import club.ryans.models.player.User;
import club.ryans.security.user.UserInfo;
import club.ryans.models.managers.UserManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class DiscordUserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
    private final DiscordService discordService;
    private final UserManager userManager;

    @Override
    public OAuth2User loadUser(final OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        Map<String, Object> attributes = new HashMap<>();

        String registrar = userRequest.getClientRegistration().getRegistrationId();
        if (!registrar.equals("discord")) {
            throw new RuntimeException(String.format("Unknown service: %s", registrar));
        }

        String token = userRequest.getAccessToken().getTokenValue();
        LOGGER.info("authentication: {}", token);

        UserInfo userInfo = discordService.getUserInfo(token);
        User user = userManager.lookupUserByDiscordInfo(userInfo);

        List<GrantedAuthority> authorityList =
                AuthorityUtils.createAuthorityList("ROLE_USER", String.format("ROLE_ID:%d", user.getId()));

        if (user.isAdmin()) {
            authorityList.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        }

        if (user.isAuthenticated()) {
            attributes.put(User.ID_PROPERTY, user.getId());
            if (!user.isRegistered()) {
                authorityList.add(new SimpleGrantedAuthority("ROLE_UNREGISTERED"));
            } else {
                authorityList.add(new SimpleGrantedAuthority("ROLE_REGISTERED"));
            }
        }

        attributes.put("oauth_token", token);
        attributes.put("name", user.getDisplayName());

        return new DefaultOAuth2User(authorityList, attributes, "name");
    }
}