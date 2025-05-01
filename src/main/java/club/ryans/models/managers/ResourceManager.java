package club.ryans.models.managers;

import club.ryans.models.Cost;
import club.ryans.models.items.Resource;
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
public class ResourceManager {
    public static final String DATA_FILE_NAME = "resources.json";

    private final DataFileManager dataFileManager;
    private final Map<Long, Resource> resourceMap = new HashMap<>();
    private final Map<Long, Resource> stfcSpaceIdMap = new HashMap<>();
    private final Map<String, Resource> nameMap = new HashMap<>();

    public ResourceManager(final DataFileManager dataFileManager, final AssetManager assetManager) {
        this.dataFileManager = dataFileManager;

        loadFile(assetManager);
    }

    public Collection<Resource> getResources() {
        return resourceMap.values();
    }

    public Resource getResource(final long id) {
        return resourceMap.get(id);
    }

    public Resource getResourceFromStfcSpaceId(final long stfcSpaceId) {
        return stfcSpaceIdMap.get(stfcSpaceId);
    }

    public Resource getResource(final String name) {
        return nameMap.get(name);
    }

    public void inflateCost(final Cost cost) {
        if (cost.getResourceId() != 0 && cost.getResource() == null) {
            cost.setResource(getResourceFromStfcSpaceId(cost.getResourceId()));
        }
    }

    private void loadFile(final AssetManager assetManager) {
        ObjectMapper mapper = Json.createObjectMapper();

        try {
            InputStream stream = dataFileManager.getStream(DATA_FILE_NAME);
            List<Resource> resources = mapper.readValue(stream, new TypeReference<List<Resource>>() {});
            for (Resource resource : resources) {
                resourceMap.put(resource.getId(), resource);
                stfcSpaceIdMap.put(resource.getStfcSpaceId(), resource);
                nameMap.put(resource.getName(), resource);

                if (Strings.isBlank(resource.getArtPath())) {
                    resource.setArtPath(assetManager.getResourcePath(resource.getArtId()));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}