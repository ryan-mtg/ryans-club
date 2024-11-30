package club.ryans.security;

import club.ryans.discord.DiscordService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class OAuthConfig {
    private final DiscordService discordService;

    @Bean
    public ClientRegistrationRepository clientRegistrationRepository(final List<ClientRegistration> registrations) {
        return new InMemoryClientRegistrationRepository(registrations);
    }

    @Bean
    public ClientRegistration discordClientRegistration() {
        return ClientRegistration.withRegistrationId("discord")
                .clientId(discordService.getClientId())
                .clientSecret(discordService.getClientSecret())
                .clientAuthenticationMethod(ClientAuthenticationMethod.POST)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .scope("identify")
                .redirectUri("{baseUrl}/login/oauth2/code/{registrationId}")
                .authorizationUri("https://discord.com/api/oauth2/authorize")
                .tokenUri("https://discord.com/api/oauth2/token")
                .userNameAttributeName("data")
                .clientName("discord")
                .build();
    }
}