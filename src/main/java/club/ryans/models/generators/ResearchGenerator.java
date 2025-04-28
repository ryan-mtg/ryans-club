package club.ryans.models.generators;

import club.ryans.models.Research;
import club.ryans.models.ResearchLevel;
import club.ryans.models.ResearchTree;
import club.ryans.models.ResearchType;
import club.ryans.models.managers.AssetManager;
import club.ryans.stfcspace.StfcSpaceClient;
import club.ryans.stfcspace.json.Field;
import club.ryans.stfcspace.json.ResearchDetails;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

@Slf4j
public class ResearchGenerator {
    private static final Map<String, BiConsumer<Research, String>> FIELD_MAP =
            new HashMap<String, BiConsumer<Research, String>>() {{
                put("research_project_name", Research::setName);
                put("research_project_description", Research::setDescription);
            }};
    private final AssetManager assetManager;
    private final StfcSpaceClient stfcSpaceClient;
    private final DataFileManager dataFileManager;

    private final Map<Long, Research> researchMap = new HashMap<>();
    private final Map<Long, ResearchTree> researchTreeMap = new HashMap<>();

    public ResearchGenerator(final AssetManager assetManager, final StfcSpaceClient stfcSpaceClient,
            final DataFileManager dataFileManager) {
        this.assetManager = assetManager;
        this.stfcSpaceClient = stfcSpaceClient;
        this.dataFileManager = dataFileManager;
    }

    public void generate() {
        List<club.ryans.stfcspace.json.Research> researches = stfcSpaceClient.research();
        for (club.ryans.stfcspace.json.Research researchJson : researches) {
            Research research = createResearch(researchJson);
            researchMap.put(research.getId(), research);
        }

        List<Field> fields = stfcSpaceClient.researches();

        Utility.applyFields(fields, this::lookup, FIELD_MAP);

        for (Research research : researchMap.values()) {
            queryResearch(research);
        }

        writeFile();
    }

    private Research lookup(final long id) {
        if (researchMap.containsKey(id)) {
            return researchMap.get(id);
        }
        LOGGER.info("failed to find research: {}", id);
        return null;
    }

    private ResearchTree lookupResearchTree(final club.ryans.stfcspace.json.ResearchTree researchTreeJson) {
        if (researchTreeMap.containsKey(researchTreeJson.getLocaId())) {
            return researchTreeMap.get(researchTreeJson.getLocaId());
        }

        ResearchTree researchTree = new ResearchTree();
        researchTree.setId(researchTreeJson.getLocaId());
        researchTree.setStfcSpaceId(researchTreeJson.getId());
        researchTree.setType(ResearchType.convert(researchTreeJson.getType()));
        return researchTree;
    }

    private void writeFile() {
        List<Research> researches = new ArrayList<>(researchMap.values());
        Collections.sort(researches, (a, b) -> {
            return a.getId() == b.getId() ? 0 : (a.getId() < b.getId() ? -1 : 1);
        });

        dataFileManager.writeFile("research.json", researches);
    }

    private Research createResearch(final club.ryans.stfcspace.json.Research researchJson) {
        Research research = new Research();
        research.setId(researchJson.getLocaId());
        research.setStfcSpaceId(researchJson.getId());
        research.setViewLevel(researchJson.getViewLevel());
        research.setUnlockLevel(researchJson.getUnlockLevel());
        research.setMaxLevel(researchJson.getMaxLevel());
        research.setArtPath(assetManager.getResearchPath(researchJson.getArtId()));
        research.setResearchTree(lookupResearchTree(researchJson.getResearchTree()));
        return research;
    }

    private void queryResearch(final Research research) {
        ResearchDetails researchDetails = stfcSpaceClient.research(research.getStfcSpaceId());

        research.setGeneration(researchDetails.getGeneration());
        if (research.getResearchTree() == null) {
            LOGGER.info("Null tree: {}, {}", research.getId(), research.getName());
        } else {
            research.setResearchType(research.getResearchTree().getType());
        }

        research.setLevels(researchDetails.getLevels().stream().map(this::createLevel).collect(Collectors.toList()));
    }

    private ResearchLevel createLevel(final club.ryans.stfcspace.json.ResearchLevel levelJson) {
        ResearchLevel level = new ResearchLevel();
        level.setId(levelJson.getId());
        level.setStrength(levelJson.getStrength());
        level.setStrengthIncrease(levelJson.getStrengthIncrease());
        level.setBuildTime(Duration.ofSeconds(levelJson.getResearchTimeSeconds()));
        level.setLatinumCost(levelJson.getLatinumCost());

        level.setCosts(levelJson.getCosts().stream().map(Utility::createCost).collect(Collectors.toList()));
        level.setRequirements(levelJson.getRequirements().stream().map(Utility::createRequirement)
                .collect(Collectors.toList()));
        level.setRewards(levelJson.getRewards().stream().map(Utility::createReward).collect(Collectors.toList()));
        return level;
    }
}
