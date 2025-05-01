package club.ryans.models.managers;

import club.ryans.models.items.Building;
import club.ryans.models.generators.DataFileManager;
import club.ryans.models.items.Inflator;
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
public class BuildingManager implements ItemManager{
    public static final String DATA_FILE_NAME = "buildings.json";

    private final Map<Long, Building> buildingMap = new HashMap<>();
    private final Map<String, Building> nameMap = new HashMap<>();

    public BuildingManager(final DataFileManager dataFileManager) {
        loadFile(dataFileManager);
    }

    @Override
    public void inflate(Inflator inflator) {
        buildingMap.values().stream().forEach(building -> building.inflate(inflator));
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

    private void loadFile(final DataFileManager dataFileManager) {
        ObjectMapper mapper = Json.createObjectMapper();

        try {
            InputStream stream = dataFileManager.getStream(DATA_FILE_NAME);
            List<Building> buildings = mapper.readValue(stream, new TypeReference<List<Building>>() {});
            for (Building building : buildings) {
                buildingMap.put(building.getId(), building);
                nameMap.put(building.getName(), building);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}