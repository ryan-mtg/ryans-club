package club.ryans.models.managers;

import club.ryans.models.Officer;
import club.ryans.models.generators.DataFileManager;
import club.ryans.utility.Json;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class OfficerManager {
    public static final String DATA_FILE_NAME = "officers.json";

    private final DataFileManager dataFileManager;
    private final Map<Long, Officer> officerMap = new HashMap<>();
    private final Map<String, Officer> nameMap = new HashMap<>();

    public OfficerManager(final DataFileManager dataFileManager) {
        this.dataFileManager = dataFileManager;

        loadFile();
    }

    public Collection<Officer> getOfficers() {
        return officerMap.values();
    }

    public Officer getOfficer(final String name) {
        return nameMap.get(name);
    }

    private Path getFilePath() {
        return Paths.get("officers.json");
    }

    private void loadFile() {
        ObjectMapper mapper = Json.createObjectMapper();

        try {
            InputStream stream = dataFileManager.getStream(DATA_FILE_NAME);
            List<Officer> officers = mapper.readValue(stream, new TypeReference<List<Officer>>() {});
            for (Officer officer : officers) {
                officerMap.put(officer.getId(), officer);
                nameMap.put(officer.getName(), officer);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}