package club.ryans.models.managers;

import club.ryans.models.Resource;
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
public class ResourceManager {
    public static final String DATA_FILE_NAME = "resources.json";

    private final DataFileManager dataFileManager;
    private final Map<Long, Resource> resourceMap = new HashMap<>();
    private final Map<String, Resource> nameMap = new HashMap<>();

    public ResourceManager(final DataFileManager dataFileManager) {
        this.dataFileManager = dataFileManager;

        loadFile();
    }

    public Collection<Resource> getResources() {
        return resourceMap.values();
    }

    public Resource getResource(final long id) {
        return resourceMap.get(id);
    }

    public Resource getResource(final String name) {
        return nameMap.get(name);
    }

    private void loadFile() {
        ObjectMapper mapper = Json.createObjectMapper();

        try {
            InputStream stream = dataFileManager.getStream(DATA_FILE_NAME);
            List<Resource> resources = mapper.readValue(stream, new TypeReference<List<Resource>>() {});
            for (Resource resource : resources) {
                resourceMap.put(resource.getId(), resource);
                nameMap.put(resource.getName(), resource);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}