package club.ryans.stfcspace;

import club.ryans.stfcspace.json.Field;
import club.ryans.stfcspace.json.Officer;
import club.ryans.stfcspace.json.Ship;
import feign.Feign;
import feign.Logger;
import feign.RequestLine;
import feign.codec.Decoder;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import feign.okhttp.OkHttpClient;
import feign.slf4j.Slf4jLogger;

import java.util.List;

public interface StfcSpaceClient {
    @RequestLine("GET /translations/en/officers")
    List<Field> officers();

    @RequestLine("GET /officer")
    List<Officer> officer();

    @RequestLine("GET /translations/en/ships")
    List<Field> ships();

    @RequestLine("GET /ship")
    List<Ship> ship();

    static String getServerUrl() {
        return "https://api.stfc.dev/v1";
    }

    static StfcSpaceClient newClient() {
        Decoder decoder = new JacksonDecoder();
        return Feign.builder().client(new OkHttpClient()).encoder(new JacksonEncoder()).decoder(decoder)
                //.errorDecoder(new ScryfallErrorDecoder(decoder))
                .logger(new Slf4jLogger(StfcSpaceClient.class)).logLevel(Logger.Level.FULL)
                .target(StfcSpaceClient.class, getServerUrl());
    }
}
