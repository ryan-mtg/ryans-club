package club.ryans.discord.client;

import club.ryans.discord.client.json.DiscordUser;
import feign.Feign;
import feign.Headers;
import feign.Logger;
import feign.Param;
import feign.RequestLine;
import feign.jackson.JacksonDecoder;
import feign.okhttp.OkHttpClient;
import feign.slf4j.Slf4jLogger;

public interface DiscordClient {
    @RequestLine("GET /users/@me")
    @Headers("Authorization: Bearer {token}")
    DiscordUser getUser(@Param("token") final String token);

    static DiscordClient newClient() {
        String url = getServerUrl();
        return Feign.builder().client(new OkHttpClient()).decoder(new JacksonDecoder())
                .logger(new Slf4jLogger(DiscordClient.class)).logLevel(Logger.Level.FULL)
                .target(DiscordClient.class, url);
    }

    static String getServerUrl() {
        return "https://discord.com/api";
    }
}
