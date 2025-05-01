package club.ryans.models.player;

import club.ryans.models.items.Building;
import club.ryans.models.items.Resource;
import club.ryans.models.ShipClass;
import club.ryans.models.accounting.ResourceAmount;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;

@Data
@Slf4j
public class PlayerItems {
    private HashMap<Long, ResourceAmount> resources = new HashMap<>();
    private HashMap<Long, BuildingLevel> buildings = new HashMap<>();
    private HashMap<Long, Ship> ships = new HashMap<>();
    private HashMap<Long, OfficerLevel> officers = new HashMap<>();
    private HashMap<Long, ResearchLevel> researchLevels = new HashMap<>();

    public PlayerItemsDelta update(final Update update) {
        PlayerItemsDelta delta = new PlayerItemsDelta();

        update.getResources().forEach(currentResourceAmount -> {
            ResourceAmount previousResourceAmount = resources.get(currentResourceAmount.getResourceId());
            delta.addResource(previousResourceAmount, currentResourceAmount);
            addResource(currentResourceAmount);
        });

        update.getBuildings().forEach(currentBuildingLevel -> {
            long buildingId = currentBuildingLevel.getBuildingId();
            BuildingLevel previousBuildingLevel = buildings.get(buildingId);
            delta.addBuilding(previousBuildingLevel, currentBuildingLevel);
            buildings.put(buildingId, currentBuildingLevel);
        });

        update.getShips().forEach(currentShip -> {
            long shipId = currentShip.getId();
            Ship previousShip = ships.get(shipId);
            delta.addShip(previousShip, currentShip);
            ships.put(shipId, currentShip);
        });

        update.getOfficers().forEach(currentOfficerLevel -> {
            long officerId = currentOfficerLevel.getOfficerId();
            OfficerLevel previousOfficerLevel = officers.get(officerId);
            delta.addOfficer(previousOfficerLevel, currentOfficerLevel);
            officers.put(officerId, currentOfficerLevel);
        });

        update.getResearchLevels().forEach(currentResearchLevel -> {
            long researchId = currentResearchLevel.getResearchId();
            ResearchLevel previousResearchLevel = researchLevels.get(researchId);
            delta.addResearch(previousResearchLevel, currentResearchLevel);
            researchLevels.put(researchId, currentResearchLevel);
        });

        return delta;
    }

    public void addResource(final ResourceAmount resourceAmount) {
        resources.put(resourceAmount.getResourceId(), resourceAmount);
    }

    public void addBuilding(final BuildingLevel buildingLevel) {
        buildings.put(buildingLevel.getBuildingId(), buildingLevel);
    }

    public void addShip(final Ship ship) {
        ships.put(ship.getId(), ship);
    }

    public void addOfficer(final OfficerLevel officerLevel) {
        officers.put(officerLevel.getOfficerId(), officerLevel);
    }

    public void addResearchLevel(final ResearchLevel researchLevel) {
        researchLevels.put(researchLevel.getResearchId(), researchLevel);
    }

    public ResourceAmount getResourceAmount(final Resource resource) {
        ResourceAmount amount = resources.get(resource.getId());
        if (amount != null) {
            return amount;
        }

        return new ResourceAmount(resource, 0L);
    }

    public Ship getShip(final ShipClass shipClass) {
        for (Ship ship : ships.values()) {
            if (ship.getShipClass().getId() == shipClass.getId()) {
                return ship;
            }
        }
        return null;
    }

    public int getResearchLevel(final long researchId) {
        ResearchLevel researchLevel = researchLevels.get(researchId);
        return researchLevel == null ? 0 : researchLevel.getLevel();
    }

    public int getOpsLevel() {
        final BuildingLevel ops = buildings.get(Building.OPS_ID);
        return ops == null ? 46 : ops.getLevel();
    }

    public int getShipTier(final ShipClass shipClass) {
        Ship ship = getShip(shipClass);
        return ship == null ? 10 : ship.getTier();
    }

    public int getBuildingLevel(final long buildingId) {
        BuildingLevel buildingLevel = buildings.get(buildingId);
        return buildingLevel == null ? 5 : buildingLevel.getLevel();
    }
}