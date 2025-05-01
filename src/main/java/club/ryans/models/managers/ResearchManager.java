package club.ryans.models.managers;

import club.ryans.models.items.Research;
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
public class ResearchManager implements ItemManager {
    public static final String DATA_FILE_NAME = "research.json";

    private final DataFileManager dataFileManager;
    private final Map<Long, Research> researchMap = new HashMap<>();
    private final Map<Long, Research> stfcSpaceIdMap = new HashMap<>();
    private final Map<String, Research> nameMap = new HashMap<>();

    public ResearchManager(final DataFileManager dataFileManager) {
        this.dataFileManager = dataFileManager;

        loadFile();
    }

    @Override
    public void inflate(final Inflator inflator) {
        researchMap.values().stream().forEach(research -> research.inflate(inflator));
    }

    public Collection<Research> getResearch() {
        return researchMap.values();
    }

    public Research getResearch(final long id) {
        return researchMap.get(id);
    }

    public Research getResearchFromStfcSpaceId(final long stfcSpaceId) {
        return stfcSpaceIdMap.get(stfcSpaceId);
    }

    public Research getResearch(final String name) {
        return nameMap.get(name);
    }

    private void loadFile() {
        ObjectMapper mapper = Json.createObjectMapper();

        try {
            InputStream stream = dataFileManager.getStream(DATA_FILE_NAME);
            List<Research> researches = mapper.readValue(stream, new TypeReference<List<Research>>() {});
            for (Research research : researches) {
                researchMap.put(research.getId(), research);
                stfcSpaceIdMap.put(research.getStfcSpaceId(), research);
                nameMap.put(research.getName(), research);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}