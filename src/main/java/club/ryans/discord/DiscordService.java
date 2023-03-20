package club.ryans.discord;

import club.ryans.discord.client.DiscordClient;
import club.ryans.discord.client.json.DiscordUser;
import club.ryans.security.user.UserInfo;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class DiscordService {
    @Value("${discord.id}")
    @Getter
    private String clientId;

    @Value("${discord.secret}")
    @Getter
    private String clientSecret;

    @Getter
    private final String authenticationToken;

    private DiscordClient discordClient = DiscordClient.newClient();

    // private final JDA jda;

    public DiscordService(@Value("${discord.token}") final String authenticationToken) {
        this.authenticationToken = authenticationToken;
        // jda = startService();
    }

    /*
    private JDA startService() {
        try {
            JDABuilder builder = JDABuilder.createDefault(authenticationToken);
            builder.enableIntents(GatewayIntent.GUILD_MEMBERS).enableIntents(GatewayIntent.GUILD_PRESENCES);
            builder.setMemberCachePolicy(MemberCachePolicy.ALL);
            // builder.addEventListeners(new DiscordEventAdapter(this, listener, homeMap, userConductor));

            JDA jda = builder.build();
            jda.awaitReady();
            return jda;
        } catch (Exception e) {
            throw new RuntimeException("Failed to start Discord Service", e);
        }
    }
     */

    public UserInfo getUserInfo(final String token) {
        DiscordUser discordUser = discordClient.getUser(token);
        String username = String.format("%s#%s", discordUser.getUsername(), discordUser.getDiscriminator());
        return new UserInfo(Long.parseLong(discordUser.getId()), username);
    }
}