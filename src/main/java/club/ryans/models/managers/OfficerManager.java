package club.ryans.models.managers;

import club.ryans.models.Officer;
import club.ryans.models.OfficerAbility;
import club.ryans.stfcspace.StfcSpaceClient;
import club.ryans.stfcspace.json.Ability;
import club.ryans.stfcspace.json.Field;
import club.ryans.utility.Json;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

@Slf4j
public class OfficerManager {
    private final StfcSpaceClient stfcSpaceClient;
    private final AssetManager assetManager;
    private final Map<Long, Officer> officerMap = new HashMap<>();
    private final Map<String, Officer> nameMap = new HashMap<>();

    private static final Map<String, BiConsumer<Officer, String>> FIELD_MAP =
            new HashMap<String, BiConsumer<Officer, String>>() {{
                put("name", Officer::setName);
                put("short_name", Officer::setShortName);
                put("narrative", Utility::ignoreField);
                put("tooltip", Utility::ignoreField);
                put("tooltip_short", Utility::ignoreField);
                put("captain_ability_name", setAbilityName(Officer::getCaptainAbility, Officer::setCaptainAbility));
                put("captain_ability_short_description", Utility::ignoreField);
                put("captain_ability_description", Utility::ignoreField);
                put("officer_ability_name", setAbilityName(Officer::getOfficerAbility, Officer::setOfficerAbility));
                put("officer_ability_short_description", Utility::ignoreField);
                put("officer_ability_description", Utility::ignoreField);
                put("below_decks_ability_name", setAbilityName(Officer::getBelowDecksAbility, Officer::setBelowDecksAbility));
                put("below_decks_ability_short_description", Utility::ignoreField);
                put("below_decks_ability_description", Utility::ignoreField);
            }};

    public OfficerManager(final StfcSpaceClient stfcSpaceClient, final AssetManager assetManager,
            final boolean loadFromFiles) {
        this.stfcSpaceClient = stfcSpaceClient;
        this.assetManager = assetManager;
        if (loadFromFiles) {
            loadFile();
        } else {
            initialize();
            writeFile();
        }
    }

    public Collection<Officer> getOfficers() {
        return officerMap.values();
    }

    public Officer getOfficer(final String name) {
        return nameMap.get(name);
    }

    private void initialize() {
        List<club.ryans.stfcspace.json.Officer> officers = stfcSpaceClient.officer();
        for (club.ryans.stfcspace.json.Officer officerJson : officers) {
            Officer officer = createOfficer(officerJson);
            officerMap.put(officer.getId(), officer);
            nameMap.put(officer.getName(), officer);
        }

        List<Field> fields = stfcSpaceClient.officers();

        for (Field field : fields) {
            long id = Long.parseLong(field.getId());
            if (id < 0) {
                continue;
            }

            Officer officer = lookup(id);

            if (FIELD_MAP.containsKey(field.getKey())) {
                FIELD_MAP.get(field.getKey()).accept(officer, field.getText());
            } else {
                LOGGER.info("Unknown officer field: {}", field.getKey());
            }
        }
    }

    private Path getFilePath() {
        return Paths.get("officers.json");
    }

    private void loadFile() {
        ObjectMapper mapper = Json.createObjectMapper();

        try {
            InputStream stream = OfficerManager.class.getResourceAsStream("/data/officers.json");
            List<Officer> officers = mapper.readValue(stream, new TypeReference<List<Officer>>() {});
            for (Officer officer : officers) {
                officerMap.put(officer.getId(), officer);
                nameMap.put(officer.getName(), officer);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void writeFile() {
        List<Officer> officers = new ArrayList<>(officerMap.values());

        try {
            ObjectMapper mapper = new ObjectMapper();
            Path p = getFilePath();
            LOGGER.info("writing file to: {}", p.toAbsolutePath());
            mapper.writerWithDefaultPrettyPrinter().writeValue(p.toFile(), officers);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Officer lookup(final long id) {
        if (officerMap.containsKey(id)) {
            return officerMap.get(id);
        }
        LOGGER.info("failed to find: {}", id);
        Officer officer = new Officer();
        officer.setId(id);
        officerMap.put(id, officer);
        return officer;
    }

    private Officer createOfficer(final club.ryans.stfcspace.json.Officer officerJson) {
        Officer officer = new Officer();
        officer.setId(officerJson.getId());
        officer.setArtId(officerJson.getArtId());
        officer.setArtPath(assetManager.getOfficerPath(officerJson.getArtId()));
        officer.setFactionId(officerJson.getFaction());
        officer.setClassId(officerJson.getClassId());
        officer.setRarity(officerJson.getRarity());
        officer.setSynergyId(officerJson.getSynergyId());
        officer.setMaxRank(officerJson.getMaxRank());
        officer.setOfficerAbility(createAbility(officerJson.getAbility()));
        officer.setCaptainAbility(createAbility(officerJson.getCaptainAbility()));
        officer.setBelowDecksAbility(createAbility(officerJson.getBelowDecksAbility()));

        return officer;
    }

    private OfficerAbility createAbility(final Ability abilityJson) {
        if (abilityJson == null) {
            return null;
        }
        OfficerAbility ability = new OfficerAbility();
        ability.setId(abilityJson.getId());
        ability.setPercentage(abilityJson.isPercentage());
        ability.setMaxLevel(abilityJson.getMaxLevel());
        ability.setArtId(abilityJson.getArtId());
        ability.setFlag(abilityJson.getFlag());
        return ability;
    }

    private static BiConsumer<Officer, String> setAbilityName(final Function<Officer, OfficerAbility> getter,
            final BiConsumer<Officer, OfficerAbility> setter) {

        return (officer, name) -> {
            OfficerAbility ability = getter.apply(officer);
            if (ability == null) {
                ability = new OfficerAbility();
                setter.accept(officer, ability);
            }
            ability.setName(name);
        };
    }
}
