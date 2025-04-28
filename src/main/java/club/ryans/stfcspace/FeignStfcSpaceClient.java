package club.ryans.stfcspace;

import club.ryans.stfcspace.json.*;
import club.ryans.stfcspace.json.System;
import club.ryans.stfcspace.json.ships.ShipClassDetails;
import feign.Feign;
import feign.Logger;
import feign.Param;
import feign.RequestLine;
import feign.codec.Decoder;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import feign.okhttp.OkHttpClient;
import feign.slf4j.Slf4jLogger;

import java.util.List;

public interface FeignStfcSpaceClient extends StfcSpaceClient{
    @RequestLine("GET /translations/en/starbase_modules.json")
    List<Field> buildings();

    @RequestLine("GET /building/summary.json")
    List<Building> building();

    @RequestLine("GET /building/{id}.json")
    BuildingDetails building(@Param("id") long id);

    @RequestLine("GET /translations/en/research.json")
    List<Field> researches();

    @RequestLine("GET /research/summary.json")
    List<Research> research();

    @RequestLine("GET /research/{id}.json")
    ResearchDetails research(@Param("id") long id);

    @RequestLine("GET /translations/en/officers.json")
    List<Field> officers();

    @RequestLine("GET /translations/en/officer_names.json")
    List<Field> officerNames();

    @RequestLine("GET /translations/en/officer_buffs.json")
    List<Field> officerBuffs();

    @RequestLine("GET /translations/en/officer_flavor_text.json")
    List<Field> officerFlavorText();

    @RequestLine("GET /officer/summary.json")
    List<Officer> officer();

    @RequestLine("GET /translations/en/factions.json")
    List<Field> factions();

    @RequestLine("GET /translations/en/materials.json")
    List<Field> resources();

    @RequestLine("GET /resource/summary.json")
    List<Resource> resource();

    @RequestLine("GET /translations/en/mission_titles.json")
    List<Field> missions();

    @RequestLine("GET /system/summary.json")
    List<System> system();

    @RequestLine("GET /translations/en/systems.json")
    List<Field> systems();

    @RequestLine("GET /mission/summary.json")
    List<Mission> mission();

    @RequestLine("GET /translations/en/ships.json")
    List<Field> ships();

    @RequestLine("GET /ship/summary.json")
    List<Ship> ship();

    @RequestLine("GET /ship/{id}.json")
    ShipClassDetails ship(@Param("id") long id);

    static String getServerUrl() {
        return "https://data.stfc.space/";
    }

    static FeignStfcSpaceClient newClient() {
        Decoder decoder = new JacksonDecoder();
        return Feign.builder().client(new OkHttpClient()).encoder(new JacksonEncoder()).decoder(decoder)
                .logger(new Slf4jLogger(FeignStfcSpaceClient.class)).logLevel(Logger.Level.FULL)
                .target(FeignStfcSpaceClient.class, getServerUrl());
    }
}