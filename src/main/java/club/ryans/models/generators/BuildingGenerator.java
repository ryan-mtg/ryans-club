package club.ryans.models.generators;

import club.ryans.models.Building;
import club.ryans.models.Cost;
import club.ryans.models.Level;
import club.ryans.models.Requirement;
import club.ryans.models.Reward;
import club.ryans.models.managers.AssetManager;
import club.ryans.models.managers.BuildingManager;
import club.ryans.stfcspace.StfcSpaceClient;
import club.ryans.stfcspace.json.BuildingDetails;
import club.ryans.stfcspace.json.Field;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
public class BuildingGenerator {
    private static final Map<String, BiConsumer<Building, String>> FIELD_MAP =
            new HashMap<String, BiConsumer<Building, String>>() {{
                put("starbase_module", BuildingGenerator::conditionallySetName);
                put("starbase_module_name", Building::setName);
                put("starbase_module_description", Building::setDescription);
            }};

    private final AssetManager assetManager;
    private final StfcSpaceClient stfcSpaceClient;
    private final DataFileManager dataFileManager;
    private final Map<Long, Building> buildingMap = new HashMap<>();

    public BuildingGenerator(final AssetManager assetManager, final StfcSpaceClient stfcSpaceClient,
            final DataFileManager dataFileManager) {
        this.assetManager = assetManager;
        this.stfcSpaceClient = stfcSpaceClient;
        this.dataFileManager = dataFileManager;
    }

    public void generate() {
        List<club.ryans.stfcspace.json.Building> buildings = stfcSpaceClient.building();
        for (club.ryans.stfcspace.json.Building buildingJson : buildings) {
            if (buildingJson.getId() < 1000) {
                Building building = createBuilding(buildingJson);
                buildingMap.put(building.getId(), building);
            }
        }

        List<Field> fields = stfcSpaceClient.buildings();

        Utility.applyFields(fields, this::lookup, FIELD_MAP);

        Map<String, Building> nameMap = new HashMap<>();
        buildingMap.forEach((id, building) -> nameMap.put(building.getName(), building));

        for (Building building : buildingMap.values()) {
            String name = building.getName();
            int length = name.length();
            if (length > 2 && name.charAt(length - 2) == ' ' && name.charAt(length - 1) != 'A') {
                String aBuilding = name.substring(0, length - 1) + 'A';
                if (nameMap.containsKey(aBuilding)) {
                    building.setArtPath(nameMap.get(aBuilding).getArtPath());
                }
            }

            LOGGER.info("fetching data for building: {}", name);
            queryBuilding(building);
        }

        writeFile();
    }

    private void queryBuilding(final Building building) {
        Utility.pause();
        BuildingDetails buildingDetails = stfcSpaceClient.building(building.getId());
        building.setLevels(buildingDetails.getLevels().stream().map(this::createLevel).collect(Collectors.toList()));
    }

    private Building lookup(final long id) {
        if (buildingMap.containsKey(id)) {
            return buildingMap.get(id);
        }
        LOGGER.info("failed to find building: {}", id);
        return null;
    }

    private Building createBuilding(final club.ryans.stfcspace.json.Building buildingJson) {
        Building building = new Building();
        building.setId(buildingJson.getId());
        building.setMaxLevel(buildingJson.getMaxLevel());
        building.setUnlockLevel(buildingJson.getUnlockLevel());
        building.setSection(buildingJson.getSection());
        building.setArtPath(assetManager.getBuildingPath((int)building.getId()));
        return building;
    }

    private Level createLevel(final club.ryans.stfcspace.json.Level levelJson) {
        Level level = new Level();
        level.setId(levelJson.getId());
        level.setPower(levelJson.getPower());
        level.setPowerIncrease(levelJson.getPowerIncrease());
        level.setStrength(levelJson.getStrength());
        level.setStrengthIncrease(levelJson.getStrengthIncrease());
        level.setBuildTime(Duration.ofSeconds(levelJson.getBuildTimeSeconds()));
        level.setLatinumCost(levelJson.getLatinumCost());

        level.setCosts(levelJson.getCosts().stream().map(this::createCost).collect(Collectors.toList()));
        level.setRequirements(levelJson.getRequirements().stream().map(this::createRequirement)
                .collect(Collectors.toList()));
        level.setRewards(levelJson.getRewards().stream().map(this::createReward).collect(Collectors.toList()));
        return level;
    }

    private Cost createCost(final club.ryans.stfcspace.json.Cost costJson) {
        Cost cost = new Cost();
        cost.setResourceId(costJson.getResourceId());
        cost.setAmount(costJson.getAmount());
        return cost;
    }

    private Requirement createRequirement(final club.ryans.stfcspace.json.Requirement requirementJson) {
        Requirement requirement = new Requirement();
        requirement.setType(requirementJson.getType());
        requirement.setRequirementId(requirementJson.getRequirementId());
        requirement.setRequiredLevel(requirementJson.getRequiredLevel());
        requirement.setPowerGain(requirementJson.getPowerGain());
        return requirement;
    }

    private Reward createReward(final club.ryans.stfcspace.json.Reward rewardJson) {
        Reward reward = new Reward();
        reward.setResourceId(rewardJson.getResourceId());
        reward.setType(rewardJson.getType());
        reward.setAmount(rewardJson.getAmount());
        return reward;
    }

    private void writeFile() {
        List<Building> buildings = new ArrayList<>(buildingMap.values());
        Collections.sort(buildings, (a, b) -> {
            return a.getId() == b.getId() ? 0 : (a.getId() < b.getId() ? -1 : 1);
        });

        dataFileManager.writeFile(BuildingManager.DATA_FILE_NAME, buildings);
    }

    private static BiConsumer<club.ryans.models.Building, String> conditionallySetNameFancy(
            final Function<Building, String> getter, final BiConsumer<Building, String> setter) {

        return (building, name) -> {
            String buildingName = getter.apply(building);
            if (buildingName == null) {
                setter.accept(building, name);
            }
        };
    }

    private static void conditionallySetName(final Building building, final String name) {
        if (building.getName() == null) {
            building.setName(name);
        }
    }
}
