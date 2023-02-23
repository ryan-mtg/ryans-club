package club.ryans.models.generators;

import club.ryans.models.ShipBonus;
import club.ryans.models.ShipClass;
import club.ryans.models.managers.AssetManager;
import club.ryans.models.managers.ShipManager;
import club.ryans.stfcspace.StfcSpaceClient;
import club.ryans.stfcspace.json.Field;
import club.ryans.stfcspace.json.Ship;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

@Slf4j
public class ShipGenerator {
    private final AssetManager assetManager;
    private final StfcSpaceClient stfcSpaceClient;
    private final DataFileManager dataFileManager;
    private final Map<Long, ShipClass> shipMap = new HashMap<>();

    private static final Map<String, BiConsumer<ShipClass, String>> FIELD_MAP =
            new HashMap<String, BiConsumer<ShipClass, String>>() {{
                put("name", ShipClass::setName);
                put("blueprint_name", ShipClass::setBlueprintName);
                put("description", ShipClass::setDescription);
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

        for (Field field : fields) {
            long id = Long.parseLong(field.getId());
            if (id < 0) {
                continue;
            }

            ShipClass shipClass = lookup(id);
            if (shipClass == null) {
                continue;
            }

            if (FIELD_MAP.containsKey(field.getKey())) {
                FIELD_MAP.get(field.getKey()).accept(shipClass, field.getText());
            } else {
                LOGGER.info("Unknown ship field: {}", field.getKey());
            }
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
        shipClass.setId(shipJson.getId());
        shipClass.setArtId(shipJson.getArtId());
        shipClass.setArtPath(assetManager.getShipPath(shipJson.getArtId()));
        shipClass.setFactionId(shipJson.getFaction());
        shipClass.setRarity(shipJson.getRarity());
        shipClass.setMaxTier(shipJson.getMaxTier());
        shipClass.setGrade(shipJson.getGrade());
        shipClass.setScrapLevel(shipJson.getScrapLevel());
        shipClass.setBlueprintsRequired(shipJson.getBlueprintsRequired());
        shipClass.setHullType(shipJson.getHullType());
        shipClass.setMaxLevel(shipJson.getMaxLevel());
        return shipClass;
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