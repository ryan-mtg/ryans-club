package club.ryans.models.managers;

import club.ryans.models.Mission;
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
import java.util.stream.Collectors;

@Slf4j
public class MissionManager {
    public static final String DATA_FILE_NAME = "missions.json";

    private final DataFileManager dataFileManager;
    private final Map<Long, Mission> missionMap = new HashMap<>();
    private final Map<String, Mission> titleMap = new HashMap<>();

    public MissionManager(final DataFileManager dataFileManager, final SystemManager systemManager) {
        this.dataFileManager = dataFileManager;

        loadFile(systemManager);
    }

    public Collection<Mission> getMissions() {
        return missionMap.values();
    }

    public Mission getMissionFromTitle(final String name) {
        return titleMap.get(name);
    }

    public Mission getMission(final long id) {
        return missionMap.get(id);
    }

    private void loadFile(final SystemManager systemManager) {
        ObjectMapper mapper = Json.createObjectMapper();

        try {
            InputStream stream = dataFileManager.getStream(DATA_FILE_NAME);
            List<Mission> missions = mapper.readValue(stream, new TypeReference<List<Mission>>() {});
            for (Mission mission : missions) {
                List<System> pickupSystems = mission.getPickupSystemIds().stream()
                        .map(id -> systemManager.getSystem(id)).collect(Collectors.toList());
                mission.setPickupSystems(pickupSystems);
                missionMap.put(mission.getId(), mission);
                titleMap.put(mission.getTitle(), mission);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}