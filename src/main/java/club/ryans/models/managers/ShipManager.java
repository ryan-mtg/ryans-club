package club.ryans.models.managers;

import club.ryans.models.ShipClass;
import club.ryans.models.ShipComponent;
import club.ryans.models.ShipTier;
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
public class ShipManager {
    public static final String DATA_FILE_NAME = "ships.json";

    private final DataFileManager dataFileManager;
    private final Map<Long, ShipClass> shipMap = new HashMap<>();
    private final Map<Long, ShipClass> stfcSpaceIdMap = new HashMap<>();
    private final Map<String, ShipClass> abilityMap = new HashMap<>();
    private final Map<String, ShipClass> nameMap = new HashMap<>();

    public ShipManager(final DataFileManager dataFileManager, final AssetManager assetManager,
            final ResourceManager resourceManager) {
        this.dataFileManager = dataFileManager;

        loadFile(assetManager, resourceManager);
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

    public ShipClass getShipClass(final long id) {
        return shipMap.get(id);
    }

    public ShipClass getShipClassFromStfcSpaceId(final long stfcSpaceId) {
        return stfcSpaceIdMap.get(stfcSpaceId);
    }

    private void loadFile(final AssetManager assetManager, final ResourceManager resourceManager) {
        ObjectMapper mapper = Json.createObjectMapper();

        try {
            InputStream stream = dataFileManager.getStream(DATA_FILE_NAME);
            List<ShipClass> shipClasses = mapper.readValue(stream, new TypeReference<List<ShipClass>>() {});
            for (ShipClass shipClass : shipClasses) {
                shipMap.put(shipClass.getId(), shipClass);
                stfcSpaceIdMap.put(shipClass.getStfcSpaceId(), shipClass);
                abilityMap.put(shipClass.getBonus().getName(), shipClass);
                nameMap.put(shipClass.getName(), shipClass);

                inflateShipClass(shipClass, assetManager, resourceManager);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void inflateShipClass(final ShipClass shipClass, final AssetManager assetManager,
            final ResourceManager resourceManager) {
        if (shipClass.getTiers() == null) {
            return;
        }

        shipClass.setArtPath(assetManager.getShipPath(shipClass.getArtId()));

        for (ShipTier tier : shipClass.getTiers()) {
            for (ShipComponent component : tier.getComponents()) {
                if (component.getBuildCosts() != null) {
                    component.getBuildCosts().forEach(resourceManager::inflateCost);
                }
            }
        }
    }
}