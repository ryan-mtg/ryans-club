package club.ryans.models.generators;

import club.ryans.models.ShipBonus;
import club.ryans.models.ShipClass;
import club.ryans.models.ShipComponent;
import club.ryans.models.ShipTier;
import club.ryans.models.managers.AssetManager;
import club.ryans.models.managers.ShipManager;
import club.ryans.stfcspace.StfcSpaceClient;
import club.ryans.stfcspace.json.Field;
import club.ryans.stfcspace.json.Ship;
import club.ryans.stfcspace.json.ships.Component;
import club.ryans.stfcspace.json.ships.ShipClassDetails;
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
public class ShipGenerator {
    private final AssetManager assetManager;
    private final StfcSpaceClient stfcSpaceClient;
    private final DataFileManager dataFileManager;
    private final Map<Long, ShipClass> shipMap = new HashMap<>();

    private static final Map<String, BiConsumer<ShipClass, String>> FIELD_MAP =
            new HashMap<String, BiConsumer<ShipClass, String>>() {{
                put("ship_name", ShipClass::setName);
                put("blueprint_name", ShipClass::setBlueprintName);
                put("ship_description", ShipClass::setDescription);
                put("bonus_name", setBonusField(ShipBonus::setName));
                put("bonus_description", setBonusField(ShipBonus::setDescription));
                put("bonus_description_short", setBonusField(ShipBonus::setShortDescription));
            }};

    public ShipGenerator(final AssetManager assetManager, final StfcSpaceClient stfcSpaceClient,
            final DataFileManager dataFileManager) {
        this.assetManager = assetManager;
        this.stfcSpaceClient = stfcSpaceClient;
        this.dataFileManager = dataFileManager;
    }

    public void generate() {
        List<Ship> ships = stfcSpaceClient.ship();
        for (club.ryans.stfcspace.json.Ship shipJson : ships) {
            ShipClass shipClass = createShip(shipJson);
            shipMap.put(shipClass.getId(), shipClass);
        }

        List<Field> fields = stfcSpaceClient.ships();

        Utility.applyFields(fields, this::lookup, FIELD_MAP);

        Map<String, ShipClass> nameMap = new HashMap<>();
        shipMap.forEach((id, shipClass) -> nameMap.put(shipClass.getName(), shipClass));

        for (ShipClass shipClass : shipMap.values()) {
            LOGGER.info("fetching data for ship: {}", shipClass.getName());
            queryShip(shipClass);
        }

        writeFile();
    }

    private void writeFile() {
        List<ShipClass> shipClasses = new ArrayList<>(shipMap.values());
        Collections.sort(shipClasses, (a, b) -> {
            return a.getId() == b.getId() ? 0 : (a.getId() < b.getId() ? -1 : 1);
        });

        dataFileManager.writeFile(ShipManager.DATA_FILE_NAME, shipClasses);
    }

    private ShipClass createShip(final club.ryans.stfcspace.json.Ship shipJson) {
        ShipClass shipClass = new ShipClass();
        shipClass.setStfcSpaceId(shipJson.getId());
        shipClass.setId(shipJson.getLocaId());
        shipClass.setArtId(shipJson.getArtId());
        shipClass.setArtPath(assetManager.getShipPath(shipJson.getArtId()));
        shipClass.setFactionId(shipJson.getFaction().getId());
        shipClass.setRarity(shipJson.getRarity());
        shipClass.setMaxTier(shipJson.getMaxTier());
        shipClass.setGrade(shipJson.getGrade());
        shipClass.setScrapLevel(shipJson.getScrapLevel());
        shipClass.setBlueprintsRequired(shipJson.getBlueprintsRequired());
        shipClass.setHullType(shipJson.getHullType());
        shipClass.setMaxLevel(shipJson.getMaxLevel());
        return shipClass;
    }

    private void queryShip(final ShipClass shipClass) {
        ShipClassDetails shipClassDetails = stfcSpaceClient.ship(shipClass.getStfcSpaceId());

        shipClass.setTiers(shipClassDetails.getTiers().stream().map(this::createTier).collect(Collectors.toList()));

        shipClass.setGrade(shipClassDetails.getGrade());
        shipClass.setArtId(shipClassDetails.getArtId());
        shipClass.setMaxTier(shipClassDetails.getMaxTier());
    }

    private ShipTier createTier(final club.ryans.stfcspace.json.ships.ShipTier tierJson) {
        ShipTier tier = new ShipTier();
        tier.setTier(tierJson.getTier());
        tier.setDuration(Duration.ofSeconds(tierJson.getDuration()));
        tier.setComponents(tierJson.getComponents().stream().map(this::createComponent).collect(Collectors.toList()));
        return tier;
    }

    private ShipComponent createComponent(final Component componentJson) {
        ShipComponent component = new ShipComponent();
        component.setId(componentJson.getLocaId());
        component.setStfcSpaceId(componentJson.getId());
        component.setArtId(component.getArtId());

        component.setOrder(componentJson.getOrder());

        component.setBuildTime(Duration.ofSeconds(componentJson.getBuildTime()));
        component.setRepairTime(Duration.ofSeconds(componentJson.getRepairTime()));

        return component;
    }

    private ShipClass lookup(final long id) {
        if (shipMap.containsKey(id)) {
            return shipMap.get(id);
        }
        LOGGER.trace("failed to find ship: {}", id);
        return null;
    }

    private static BiConsumer<ShipClass, String> setBonusField(final BiConsumer<ShipBonus, String> fieldSetter) {
        return (shipClass, value) -> {
            ShipBonus bonus = shipClass.getBonus();
            fieldSetter.accept(bonus, value);
        };
    }
}