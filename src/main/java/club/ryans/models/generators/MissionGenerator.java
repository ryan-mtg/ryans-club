package club.ryans.models.generators;

import club.ryans.models.Mission;
import club.ryans.models.managers.AssetManager;
import club.ryans.models.managers.MissionManager;
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
public class MissionGenerator {
    private static final String DATA_FILE_NAME = "missions.json";
    private static final Map<String, BiConsumer<Mission, String>> FIELD_MAP =
            new HashMap<String, BiConsumer<Mission, String>>() {{
                put("title", Mission::setTitle);
            }};

    private final AssetManager assetManager;
    private final StfcSpaceClient stfcSpaceClient;
    private final DataFileManager dataFileManager;
    private final Map<Long, Mission> missionMap = new HashMap<>();

    public MissionGenerator(final AssetManager assetManager, final StfcSpaceClient stfcSpaceClient,
            final DataFileManager dataFileManager) {
        this.assetManager = assetManager;
        this.stfcSpaceClient = stfcSpaceClient;
        this.dataFileManager = dataFileManager;
    }

    public void generate() {
        List<club.ryans.stfcspace.json.Mission> missions = stfcSpaceClient.mission();
        for (club.ryans.stfcspace.json.Mission missionJson : missions) {
            Mission mission = createMission(missionJson);
            if (missionMap.containsKey(mission.getId())) {
                LOGGER.info("duplicate mission: {}", mission.getId());
            }
            missionMap.put(mission.getId(), mission);
        }

        List<Field> fields = stfcSpaceClient.missions();
        Utility.applyFields(fields, this::lookup, FIELD_MAP);

        writeFile();
    }

    private Mission lookup(final long id) {
        if (missionMap.containsKey(id)) {
            return missionMap.get(id);
        }
        LOGGER.info("failed to find mission: {}", id);
        return null;
    }

    private Mission createMission(final club.ryans.stfcspace.json.Mission missionJson) {
        Mission mission = new Mission();
        mission.setId(missionJson.getId());
        mission.setRecommendedLevel(missionJson.getRecommendedLevel());
        mission.setPickupSystemIds(missionJson.getPickupSystems());
        mission.setWarp(missionJson.getWarp());
        mission.setWarpForCompletion(missionJson.getWarpForCompletion());
        mission.setWarpWithSuperHighway(missionJson.getWarpWithSuperhighway());
        mission.setWarpForCompletionWithSuperHighway(missionJson.getWarpForCompletionWithSuperhighway());
        return mission;
    }

    private void writeFile() {
        List<Mission> missions = new ArrayList<>(missionMap.values());
        Collections.sort(missions, (a, b) -> {
            return a.getId() == b.getId() ? 0 : (a.getId() < b.getId() ? -1 : 1);
        });

        dataFileManager.writeFile(MissionManager.DATA_FILE_NAME, missions);
    }
}
