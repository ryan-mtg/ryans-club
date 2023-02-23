package club.ryans.models.generators;

import club.ryans.models.Officer;
import club.ryans.models.OfficerAbility;
import club.ryans.models.managers.AssetManager;
import club.ryans.models.managers.OfficerManager;
import club.ryans.stfcspace.StfcSpaceClient;
import club.ryans.stfcspace.json.Ability;
import club.ryans.stfcspace.json.Field;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

@Slf4j
public class OfficerGenerator {
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

    private final AssetManager assetManager;
    private final StfcSpaceClient stfcSpaceClient;
    private final DataFileManager dataFileManager;
    private final Map<Long, Officer> officerMap = new HashMap<>();

    public OfficerGenerator(final AssetManager assetManager, final StfcSpaceClient stfcSpaceClient,
            final DataFileManager dataFileManager) {
        this.assetManager = assetManager;
        this.stfcSpaceClient = stfcSpaceClient;
        this.dataFileManager = dataFileManager;
    }

    public void generate() {
        List<club.ryans.stfcspace.json.Officer> officers = stfcSpaceClient.officer();
        for (club.ryans.stfcspace.json.Officer officerJson : officers) {
            Officer officer = createOfficer(officerJson);
            officerMap.put(officer.getId(), officer);
        }

        List<Field> fields = stfcSpaceClient.officers();

        for (Field field : fields) {
            long id = Long.parseLong(field.getId());
            if (id < 0) {
                continue;
            }

            Officer officer = lookup(id);
            if (officer == null) {
                continue;
            }

            if (FIELD_MAP.containsKey(field.getKey())) {
                FIELD_MAP.get(field.getKey()).accept(officer, field.getText());
            } else {
                LOGGER.info("Unknown officer field: {}", field.getKey());
            }
        }

        writeFile();
    }

    private void writeFile() {
        List<Officer> officers = new ArrayList<>(officerMap.values());
        Collections.sort(officers, (a, b) -> {
            return a.getId() == b.getId() ? 0 : (a.getId() < b.getId() ? -1 : 1);
        });

        dataFileManager.writeFile(OfficerManager.DATA_FILE_NAME, officers);
    }

    private Officer lookup(final long id) {
        if (officerMap.containsKey(id)) {
            return officerMap.get(id);
        }
        LOGGER.info("failed to find: {}", id);
        return null;
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

    private static BiConsumer<club.ryans.models.Officer, String> setAbilityName(final Function<club.ryans.models.Officer, OfficerAbility> getter,
            final BiConsumer<club.ryans.models.Officer, OfficerAbility> setter) {

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
