package club.ryans.discord.client.json;

import lombok.Data;

@Data
public class DiscordUser {
    private String id;
    private String username;
    private String discriminator;
}
