package club.ryans.models.generators;

import club.ryans.models.Resource;
import club.ryans.models.managers.AssetManager;
import club.ryans.models.managers.ResourceManager;
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
public class ResourceGenerator {
    private static final Map<String, BiConsumer<Resource, String>> FIELD_MAP =
            new HashMap<String, BiConsumer<Resource, String>>() {{
                put("name", Resource::setName);
                put("name_short", Resource::setShortName);
                put("description", Resource::setDescription);
            }};

    private final AssetManager assetManager;
    private final StfcSpaceClient stfcSpaceClient;
    private final DataFileManager dataFileManager;
    private final Map<Long, Resource> resourceMap = new HashMap<>();

    public ResourceGenerator(final AssetManager assetManager, final StfcSpaceClient stfcSpaceClient,
            final DataFileManager dataFileManager) {
        this.assetManager = assetManager;
        this.stfcSpaceClient = stfcSpaceClient;
        this.dataFileManager = dataFileManager;
    }

    public void generate() {
        List<club.ryans.stfcspace.json.Resource> resources = stfcSpaceClient.resource();
        for (club.ryans.stfcspace.json.Resource resourceJson : resources) {
            Resource resource = createResource(resourceJson);
            resourceMap.put(resource.getId(), resource);
        }

        List<Field> fields = stfcSpaceClient.resources();

        Utility.applyFields(fields, this::lookup, FIELD_MAP);

        writeFile();
    }

    private Resource lookup(final long id) {
        if (resourceMap.containsKey(id)) {
            return resourceMap.get(id);
        }
        LOGGER.info("failed to find resource: {}", id);
        return null;
    }

    private Resource createResource(final club.ryans.stfcspace.json.Resource resourceJson) {
        Resource resource = new Resource();
        resource.setId(resourceJson.getId());
        resource.setGrade(resourceJson.getGrade());
        resource.setRarity(resourceJson.getRarity());
        resource.setArtId(resourceJson.getArtId());
        resource.setArtPath(assetManager.getResourcePath(resource.getArtId()));
        return resource;
    }

    private void writeFile() {
        List<Resource> resources = new ArrayList<>(resourceMap.values());
        Collections.sort(resources, (a, b) -> {
            return a.getId() == b.getId() ? 0 : (a.getId() < b.getId() ? -1 : 1);
        });

        dataFileManager.writeFile(ResourceManager.DATA_FILE_NAME, resources);
    }
}
