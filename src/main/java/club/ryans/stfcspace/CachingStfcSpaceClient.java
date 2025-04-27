package club.ryans.stfcspace;

import club.ryans.models.generators.Utility;
import club.ryans.stfcspace.json.Building;
import club.ryans.stfcspace.json.BuildingDetails;
import club.ryans.stfcspace.json.Field;
import club.ryans.stfcspace.json.Mission;
import club.ryans.stfcspace.json.Officer;
import club.ryans.stfcspace.json.Research;
import club.ryans.stfcspace.json.Resource;
import club.ryans.stfcspace.json.Ship;
import club.ryans.stfcspace.json.System;
import club.ryans.stfcspace.json.ships.ShipClassDetails;
import club.ryans.utility.Json;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.function.Supplier;

@Slf4j
public class CachingStfcSpaceClient implements StfcSpaceClient {
    private static final int CACHE_AGE_DAYS = 21;
    private static final String CACHE_DIRECTORY = "data-cache";
    private FeignStfcSpaceClient feignClient;
    private File cacheDirectory;
    private ObjectMapper mapper = Json.createObjectMapper();

    public CachingStfcSpaceClient() {
        feignClient = FeignStfcSpaceClient.newClient();
        cacheDirectory = new File(CACHE_DIRECTORY);
    }

    @Override
    public List<Field> buildings() {
        return getCache("building-fields", new TypeReference<List<Field>>(){}, () -> feignClient.buildings());
    }

    @Override
    public List<Building> building() {
        return getCache("building-summary", new TypeReference<List<Building>>(){}, () -> feignClient.building());
    }

    @Override
    public BuildingDetails building(long id) {
        String key = String.format("building-details-%d", id);
        return getCache(key, new TypeReference<BuildingDetails>(){}, () -> {
            Utility.pause();
            return feignClient.building(id);
        });
    }

    @Override
    public List<Field> researches() {
        return getCache("research-fields", new TypeReference<List<Field>>(){}, () -> feignClient.researches());
    }

    @Override
    public List<Research> research() {
        return getCache("research-summary", new TypeReference<List<Research>>(){}, () -> feignClient.research());
    }

    @Override
    public List<Field> officers() {
        return getCache("officer-fields", new TypeReference<List<Field>>(){}, () -> feignClient.officers());
    }

    @Override
    public List<Field> officerNames() {
        return getCache("officer-name-fields", new TypeReference<List<Field>>(){},
                () -> feignClient.officerNames());
    }

    @Override
    public List<Field> officerBuffs() {
        return getCache("officer-buff-fields", new TypeReference<List<Field>>(){},
                () -> feignClient.officerBuffs());
    }

    @Override
    public List<Field> officerFlavorText() {
        return getCache("officer-flavor-text-fields", new TypeReference<List<Field>>(){},
                () -> feignClient.officerFlavorText());
    }

    @Override
    public List<Officer> officer() {
        return getCache("officer-summary", new TypeReference<List<Officer>>(){}, () -> feignClient.officer());
    }

    @Override
    public List<Field> factions() {
        return getCache("faction-fields", new TypeReference<List<Field>>(){}, () -> feignClient.factions());
    }

    @Override
    public List<Field> resources() {
        return getCache("resource-fields", new TypeReference<List<Field>>(){}, () -> feignClient.resources());
    }

    @Override
    public List<Resource> resource() {
        return getCache("resource-summary", new TypeReference<List<Resource>>(){}, () -> feignClient.resource());
    }

    @Override
    public List<Field> missions() {
        return getCache("mission-fields", new TypeReference<List<Field>>(){}, () -> feignClient.missions());
    }

    @Override
    public List<Mission> mission() {
        return getCache("mission-summary", new TypeReference<List<Mission>>(){}, () -> feignClient.mission());
    }

    @Override
    public List<Field> systems() {
        return getCache("system-fields", new TypeReference<List<Field>>(){}, () -> feignClient.systems());
    }

    @Override
    public List<System> system() {
        return getCache("system-summary", new TypeReference<List<System>>(){}, () -> feignClient.system());
    }

    @Override
    public List<Field> ships() {
        return getCache("ship-fields", new TypeReference<List<Field>>(){}, () -> feignClient.ships());
    }

    @Override
    public List<Ship> ship() {
        return getCache("ship-summary", new TypeReference<List<Ship>>(){}, () -> feignClient.ship());
    }

    @Override
    public ShipClassDetails ship(long id) {
        String key = String.format("ship-details-%d", id);
        return getCache(key, new TypeReference<ShipClassDetails>(){}, () -> {
            Utility.pause();
            return feignClient.ship(id);
        });
    }

    public <ValueType> ValueType getCache(final String key, final TypeReference<ValueType> typeReference,
            final Supplier<ValueType> supplier) {
        File cacheFile = getCacheFile(key);
        if (hasCache(cacheFile)) {
            return readFile(cacheFile, typeReference);
        }

        ValueType value = supplier.get();
        store(cacheFile, value);
        return value;
    }

    private File getCacheFile(final String key) {
        String fileName = key + ".json";
        return new File(cacheDirectory, fileName);
    }

    private boolean hasCache(final File cacheFile) {
        if (!cacheFile.exists()) {
            return false;
        }

        Instant modifiedTime = Instant.ofEpochMilli(cacheFile.lastModified());
        Duration fileAge = Duration.between(modifiedTime, Instant.now());

        return fileAge.toDays() < CACHE_AGE_DAYS;
    }

    private void store(final File cacheFile, final Object contents) {
        if (cacheFile.exists()) {
            LOGGER.info("Overwriting cache file: {}", cacheFile.getAbsolutePath());
        } else {
            LOGGER.info("Writing new cache file: {}", cacheFile.getAbsolutePath());
        }

        writeFile(cacheFile, contents);
    }

    private void writeFile(final File file, final Object contents) {
        try {
            if (!cacheDirectory.exists()) {
                cacheDirectory.mkdirs();
            }

            mapper.writerWithDefaultPrettyPrinter().writeValue(file, contents);
        } catch (Exception e) {
            LOGGER.error("Failed to write cache file: ", e);
        }
    }

    private <T> T readFile(final File file, final TypeReference<T> typeReference) {
        try {
            return mapper.readValue(file, typeReference);
        } catch (Exception e) {
            LOGGER.error("Failed to read cache file: ", e);
            throw new RuntimeException(e);
        }
    }
}