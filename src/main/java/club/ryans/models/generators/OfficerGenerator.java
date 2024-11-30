package club.ryans.models.generators;

import club.ryans.models.Officer;
import club.ryans.models.OfficerAbility;
import club.ryans.models.Rarity;
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
import java.util.stream.Collectors;

@Slf4j
public class OfficerGenerator {
    private static final String SYNGERGY_GROUP_KEY = "fleet_officer_synergy_group";
    private static final String DIVISION_NAME_KEY = "fleet_officer_division_name";
    private static final String FACTION_RANK_KEY = "factions_rank";
    private static final Map<String, BiConsumer<Officer, String>> FIELD_MAP =
            new HashMap<String, BiConsumer<Officer, String>>() {{
                put("name", Officer::setName);
                put("officer_name", Officer::setName);
                put("short_name", Officer::setShortName);
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

    private Map<Integer, String> synergyGroupMap = null;
    private Map<Integer, String> divisionNameMap = null;
    private Map<Integer, String> factionRankMap = null;

    public OfficerGenerator(final AssetManager assetManager, final StfcSpaceClient stfcSpaceClient,
            final DataFileManager dataFileManager) {
        this.assetManager = assetManager;
        this.stfcSpaceClient = stfcSpaceClient;
        this.dataFileManager = dataFileManager;
    }

    public void generate() {
        List<Field> officerFields = stfcSpaceClient.officers();

        loadOfficerMaps(officerFields);

        List<club.ryans.stfcspace.json.Officer> officers = stfcSpaceClient.officer();
        for (club.ryans.stfcspace.json.Officer officerJson : officers) {
            Officer officer = createOfficer(officerJson);
            officerMap.put(officer.getId(), officer);
        }

        Utility.applyFields(officerFields, this::lookup, FIELD_MAP);
        Utility.applyFields(stfcSpaceClient.officerNames(), this::lookup, FIELD_MAP);
        Utility.applyFields(stfcSpaceClient.officerBuffs(), this::lookup, FIELD_MAP);
        Utility.applyFields(stfcSpaceClient.officerFlavorText(), this::lookup, FIELD_MAP);

        writeFile();

        List<Field> factionFields = stfcSpaceClient.factions();
        loadFactionMaps(factionFields);

        writeFactionFile();
    }

    private void writeFile() {
        List<Officer> officers = new ArrayList<>(officerMap.values());
        Collections.sort(officers, (a, b) -> {
            return a.getId() == b.getId() ? 0 : (a.getId() < b.getId() ? -1 : 1);
        });

        dataFileManager.writeFile(OfficerManager.DATA_FILE_NAME, officers);
    }

    private void writeFactionFile() {
        List<Map.Entry<Integer, String>> factionRanks = factionRankMap.entrySet().stream().collect(Collectors.toList());
        Collections.sort(factionRanks, (a, b) -> a.getKey().compareTo(b.getKey()));

        dataFileManager.writeFile(OfficerManager.FACTION_DATA_FILE_NAME, factionRanks);
    }

    private Officer lookup(final long id) {
        if (officerMap.containsKey(id)) {
            return officerMap.get(id);
        }
        return null;
    }

    private Officer createOfficer(final club.ryans.stfcspace.json.Officer officerJson) {
        Officer officer = new Officer();
        officer.setId(officerJson.getLocaId());
        officer.setStfcSpaceId(officerJson.getId());
        officer.setArtId(officerJson.getArtId());
        officer.setArtPath(assetManager.getOfficerPath(officerJson.getArtId()));
        officer.setFactionId(officerJson.getFaction());
        officer.setDivisionName(divisionNameMap.get(officerJson.getClassId()));
        officer.setRarity(Rarity.fromInt(officerJson.getRarity()));
        officer.setSynergyGroup(synergyGroupMap.get(officerJson.getSynergyId()));
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

    private void loadOfficerMaps(final List<Field> fields) {
        synergyGroupMap = new HashMap<>();
        divisionNameMap = new HashMap<>();
        for (Field field : fields) {
            switch (field.getKey()) {
                case SYNGERGY_GROUP_KEY:
                    synergyGroupMap.put(Integer.parseInt(field.getId()), field.getText());
                    break;
                case DIVISION_NAME_KEY:
                    divisionNameMap.put(Integer.parseInt(field.getId()), field.getText());
                    break;
            }
        }
    }

    private void loadFactionMaps(final List<Field> fields) {
        factionRankMap = new HashMap<>();
        for (Field field : fields) {
            switch (field.getKey()) {
                case FACTION_RANK_KEY:
                    factionRankMap.put(Integer.parseInt(field.getId()), field.getText());
                    break;
            }
        }
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
