package club.ryans.models.managers;

import club.ryans.models.Building;
import club.ryans.models.Cost;
import club.ryans.models.Level;
import club.ryans.models.generators.DataFileManager;
import club.ryans.utility.Json;
import club.ryans.utility.Strings;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class BuildingManager {
    public static final String DATA_FILE_NAME = "buildings.json";

    private final DataFileManager dataFileManager;
    private final Map<Long, Building> buildingMap = new HashMap<>();
    private final Map<String, Building> nameMap = new HashMap<>();

    public BuildingManager(final DataFileManager dataFileManager, final AssetManager assetManager,
            final ResourceManager resourceManager) {
        this.dataFileManager = dataFileManager;

        loadFile(assetManager, resourceManager);
    }

    public Collection<Building> getBuildings() {
        return buildingMap.values();
    }

    public Building getBuilding(final String name) {
        return nameMap.get(name);
    }

    public Building getBuilding(final long id) {
        return buildingMap.get(id);
    }

    private void loadFile(final AssetManager assetManager, final ResourceManager resourceManager) {
        ObjectMapper mapper = Json.createObjectMapper();

        try {
            InputStream stream = dataFileManager.getStream(DATA_FILE_NAME);
            List<Building> buildings = mapper.readValue(stream, new TypeReference<List<Building>>() {});
            for (Building building : buildings) {
                buildingMap.put(building.getId(), building);
                nameMap.put(building.getName(), building);
                inflateBuilding(building, assetManager, resourceManager);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void inflateBuilding(final Building building, final AssetManager assetManager,
            final ResourceManager resourceManager) {

        if (Strings.isBlank(building.getArtPath())) {
            building.setArtPath(assetManager.getBuildingPath((int) building.getId()));
        }

        if (building.getLevels() == null) {
            return;
        }

        for (Level level : building.getLevels()) {
            level.getCosts().forEach(resourceManager::inflateCost);
        }
    }
}