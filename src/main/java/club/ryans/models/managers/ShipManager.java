package club.ryans.models.managers;

import club.ryans.models.ShipClass;
import club.ryans.models.ShipBonus;
import club.ryans.stfcspace.StfcSpaceClient;
import club.ryans.stfcspace.json.Field;
import club.ryans.utility.Json;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

@Slf4j
public class ShipManager {
    private final StfcSpaceClient stfcSpaceClient;
    private final AssetManager assetManager;
    private final Map<Long, ShipClass> shipMap = new HashMap<>();
    private final Map<String, ShipClass> abilityMap = new HashMap<>();
    private final Map<String, ShipClass> nameMap = new HashMap<>();

    private static final Map<String, BiConsumer<ShipClass, String>> FIELD_MAP =
            new HashMap<String, BiConsumer<ShipClass, String>>() {{
                put("name", ShipClass::setName);
                put("blueprint_name", ShipClass::setBlueprintName);
                put("description", ShipClass::setDescription);
                put("bonus_name", setBonusField(ShipBonus::setName));
                put("bonus_description", setBonusField(ShipBonus::setDescription));
                put("bonus_description_short", setBonusField(ShipBonus::setShortDescription));
            }};

    public ShipManager(final StfcSpaceClient stfcSpaceClient, final AssetManager assetManager,
            final boolean loadFromFiles) {
        this.stfcSpaceClient = stfcSpaceClient;
        this.assetManager = assetManager;

        if (loadFromFiles) {
            loadFile();
        } else {
            loadFromStfc();
            writeFile();
        }
    }

    public Collection<ShipClass> getShips() {
        return shipMap.values();
    }

    public ShipClass getShipClassFromAbility(final String ability) {
        return abilityMap.get(ability);
    }

    public ShipClass getShipClassFromName(final String name) {
        return nameMap.get(name);
    }

    private void loadFromStfc() {
        List<club.ryans.stfcspace.json.Ship> ships = stfcSpaceClient.ship();
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

        for (ShipClass shipClass : shipMap.values()) {
            abilityMap.put(shipClass.getBonus().getName(), shipClass);
            nameMap.put(shipClass.getName(), shipClass);
        }
    }

    private Path getFilePath() {
        return Paths.get("ships.json");
    }

    private void loadFile() {
        Path p = getFilePath();
        ObjectMapper mapper = Json.createObjectMapper();

        try {
            List<ShipClass> shipClasses = mapper.readValue(p.toFile(), new TypeReference<List<ShipClass>>() {});
            for (ShipClass shipClass : shipClasses) {
                shipMap.put(shipClass.getId(), shipClass);
                abilityMap.put(shipClass.getBonus().getName(), shipClass);
                nameMap.put(shipClass.getName(), shipClass);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void writeFile() {
        List<ShipClass> shipClasses = shipMap.values().stream().collect(Collectors.toList());

        try {
            ObjectMapper mapper = new ObjectMapper();
            Path p = getFilePath();
            LOGGER.info("writing file to: {}", p.toAbsolutePath());
            mapper.writerWithDefaultPrettyPrinter().writeValue(p.toFile(), shipClasses);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ShipClass lookup(final long id) {
        if (shipMap.containsKey(id)) {
            return shipMap.get(id);
        }
        LOGGER.trace("failed to find ship: {}", id);
        return null;
        /*
        Ship ship = new Ship();
        ship.setId(id);
        shipMap.put(id, ship);
        return ship;
         */
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

    private static BiConsumer<ShipClass, String> setBonusField(final BiConsumer<ShipBonus, String> fieldSetter) {
        return (shipClass, value) -> {
            ShipBonus bonus = shipClass.getBonus();
            fieldSetter.accept(bonus, value);
        };
    }
}