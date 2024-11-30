package club.ryans.models.managers;

import club.ryans.models.System;
import club.ryans.models.generators.DataFileManager;
import club.ryans.utility.Json;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class SystemManager {
    public static final String DATA_FILE_NAME = "systems.json";

    private final DataFileManager dataFileManager;
    private final Map<Long, System> systemMap = new HashMap<>();
    private final Map<String, System> nameMap = new HashMap<>();

    public SystemManager(final DataFileManager dataFileManager) {
        this.dataFileManager = dataFileManager;

        loadFile();
    }

    public Collection<System> getSystems() {
        return systemMap.values();
    }

    public System getSystemFromName(final String name) {
        return nameMap.get(name);
    }

    public System getSystem(final long id) {
        return systemMap.get(id);
    }

    private void loadFile() {
        ObjectMapper mapper = Json.createObjectMapper();

        try {
            InputStream stream = dataFileManager.getStream(DATA_FILE_NAME);
            List<System> systems = mapper.readValue(stream, new TypeReference<List<System>>() {});
            for (System system : systems) {
                systemMap.put(system.getId(), system);
                nameMap.put(system.getName(), system);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}