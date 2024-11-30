package club.ryans.models.player;

import club.ryans.models.accounting.ResourceAmount;

import java.util.Collection;
import java.util.HashMap;

public class PlayerItemsDelta {
    private HashMap<Long, ResourceAmountDelta> resources = new HashMap<>();
    private HashMap<Long, BuildingLevelDelta> buildings = new HashMap<>();
    private HashMap<Long, ShipDelta> ships = new HashMap<>();
    private HashMap<Long, OfficerLevelDelta> officers = new HashMap<>();
    private HashMap<Long, ResearchLevelDelta> researchLevels = new HashMap<>();

    public void addResource(final ResourceAmount previousAmount, final ResourceAmount currentAmount) {
        ResourceAmountDelta delta = new ResourceAmountDelta(previousAmount, currentAmount);
        resources.put(delta.getResourceId(), delta);
    }

    public void addBuilding(final BuildingLevel previousBuildingLevel, final BuildingLevel currentBuildingLevel) {
        BuildingLevelDelta delta = new BuildingLevelDelta(previousBuildingLevel, currentBuildingLevel);
        buildings.put(delta.getBuildingId(), delta);
    }

    public void addShip(final Ship previousShip, final Ship currentShip) {
        ShipDelta delta = new ShipDelta(previousShip, currentShip);
        ships.put(delta.getShipId(), delta);
    }

    public void addOfficer(final OfficerLevel previousOfficerLevel, final OfficerLevel currentOfficerLevel) {
        OfficerLevelDelta delta = new OfficerLevelDelta(previousOfficerLevel, currentOfficerLevel);
        officers.put(delta.getOfficerId(), delta);
    }

    public void addResearch(final ResearchLevel previousResearchLevel, final ResearchLevel currentResearchLevel) {
        ResearchLevelDelta delta = new ResearchLevelDelta(previousResearchLevel, currentResearchLevel);
        researchLevels.put(delta.getResearchId(), delta);
    }

    public Collection<ResourceAmountDelta> getResources() {
        return resources.values();
    }

    public Collection<BuildingLevelDelta> getBuildings() {
        return buildings.values();
    }

    public Collection<ShipDelta> getships() {
        return ships.values();
    }

    public Collection<OfficerLevelDelta> getOfficers() {
        return officers.values();
    }

    public Collection<ResearchLevelDelta> getResearchLevels() {
        return researchLevels.values();
    }
}