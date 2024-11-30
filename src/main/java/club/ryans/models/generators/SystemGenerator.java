package club.ryans.models.generators;

import club.ryans.models.System;
import club.ryans.models.managers.AssetManager;
import club.ryans.models.managers.SystemManager;
import club.ryans.stfcspace.StfcSpaceClient;
import club.ryans.stfcspace.json.Field;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

@Slf4j
public class SystemGenerator {
    private static final String DATA_FILE_NAME = "systems.json";
    private static final Map<String, BiConsumer<System, String>> FIELD_MAP =
            new HashMap<String, BiConsumer<System, String>>() {{
                put("title", System::setName);
                put("narrative", System::setNarrative);
                put("dialogue", System::setDialogue);
                put("dilemma_description", System::setDilemmaDescription);
            }};

    private final AssetManager assetManager;
    private final StfcSpaceClient stfcSpaceClient;
    private final DataFileManager dataFileManager;
    private final Map<Long, System> systemMap = new HashMap<>();

    public SystemGenerator(final AssetManager assetManager, final StfcSpaceClient stfcSpaceClient,
            final DataFileManager dataFileManager) {
        this.assetManager = assetManager;
        this.stfcSpaceClient = stfcSpaceClient;
        this.dataFileManager = dataFileManager;
    }

    public void generate() {
        List<club.ryans.stfcspace.json.System> systems = stfcSpaceClient.system();
        for (club.ryans.stfcspace.json.System systemJson : systems) {
            System system = createSystem(systemJson);
            if (systemMap.containsKey(system.getId())) {
                LOGGER.info("duplicate system: {}", system.getId());
            }
            systemMap.put(system.getId(), system);
        }

        List<Field> fields = stfcSpaceClient.systems();
        Utility.applyFields(fields, this::lookup, FIELD_MAP);

        writeFile();
    }

    private System lookup(final long id) {
        if (systemMap.containsKey(id)) {
            return systemMap.get(id);
        }
        LOGGER.info("failed to find system: {}", id);
        return null;
    }

    private System createSystem(final club.ryans.stfcspace.json.System systemJson) {
        System system = new System();
        system.setId(systemJson.getId());
        system.setLevel(systemJson.getLevel());
        system.setX(systemJson.getX());
        system.setY(systemJson.getY());
        system.setWarp(systemJson.getWarp());
        system.setWarpWithSuperHighway(systemJson.getWarpWithSuperhighway());
        system.setDeepSpace(systemJson.isDeepSpace());
        system.setMining(systemJson.isMines());
        system.setHousing(systemJson.isHousing());
        system.setMissions(systemJson.isMissions());
        system.setWaveDefense(systemJson.isWaveDefense());
        system.setHazards(systemJson.isHazardsEnabled());
        return system;
    }

    private void writeFile() {
        List<System> systems = new ArrayList<>(systemMap.values());
        Collections.sort(systems, (a, b) -> {
            return a.getId() == b.getId() ? 0 : (a.getId() < b.getId() ? -1 : 1);
        });

        dataFileManager.writeFile(SystemManager.DATA_FILE_NAME, systems);
    }
}
