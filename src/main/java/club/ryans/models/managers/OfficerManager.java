package club.ryans.models.managers;

import club.ryans.models.Officer;
import club.ryans.models.generators.DataFileManager;
import club.ryans.utility.Json;
import club.ryans.utility.Strings;
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
    public static final String FACTION_DATA_FILE_NAME = "factions.json";

    private final DataFileManager dataFileManager;
    private final Map<Long, Officer> officerMap = new HashMap<>();
    private final Map<Long, Officer> stfcSpaceIdMap = new HashMap<>();
    private final Map<String, Officer> nameMap = new HashMap<>();

    public OfficerManager(final DataFileManager dataFileManager, final AssetManager assetManager) {
        this.dataFileManager = dataFileManager;

        loadFile(assetManager);
    }

    public Collection<Officer> getOfficers() {
        return officerMap.values();
    }

    public Officer getOfficer(final long id) {
        return officerMap.get(id);
    }

    public Officer getOfficerFromStfcSpaceId(final long stfcSpaceId) {
        return stfcSpaceIdMap.get(stfcSpaceId);
    }

    public Officer getOfficer(final String name) {
        return nameMap.get(name);
    }

    private Path getFilePath() {
        return Paths.get("officers.json");
    }

    private void loadFile(final AssetManager assetManager) {
        ObjectMapper mapper = Json.createObjectMapper();

        try {
            InputStream stream = dataFileManager.getStream(DATA_FILE_NAME);
            List<Officer> officers = mapper.readValue(stream, new TypeReference<List<Officer>>() {});
            for (Officer officer : officers) {
                officerMap.put(officer.getId(), officer);
                stfcSpaceIdMap.put(officer.getStfcSpaceId(), officer);
                nameMap.put(officer.getName(), officer);

                if (Strings.isBlank(officer.getArtPath())) {
                    officer.setArtPath(assetManager.getOfficerPath(officer.getArtId()));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}