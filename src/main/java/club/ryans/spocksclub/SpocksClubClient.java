package club.ryans.spocksclub;

import feign.Body;
import feign.Feign;
import feign.Headers;
import feign.Logger;
import feign.Param;
import feign.RequestLine;
import feign.codec.Decoder;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import feign.okhttp.OkHttpClient;
import feign.slf4j.Slf4jLogger;

public interface SpocksClubClient {
    @RequestLine("POST /sync/ingress/")
    @Headers({"stfc-sync-token: {syncToken}"})
    @Body("{body}")
    void sync(@Param("syncToken") String syncToken, @Param("body") String body);

    static String getServerUrl() {
        return "https://spocks.club/";
    }

    static SpocksClubClient newClient() {
        Decoder decoder = new JacksonDecoder();
        return Feign.builder().client(new OkHttpClient()).encoder(new JacksonEncoder()).decoder(decoder)
                .logger(new Slf4jLogger(SpocksClubClient.class)).logLevel(Logger.Level.BASIC)
                .target(SpocksClubClient.class, getServerUrl());
    }
}
