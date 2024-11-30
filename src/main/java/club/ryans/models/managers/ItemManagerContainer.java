package club.ryans.models.managers;

import club.ryans.models.Building;
import club.ryans.models.Mission;
import club.ryans.models.Officer;
import club.ryans.models.Research;
import club.ryans.models.Resource;
import club.ryans.models.ShipClass;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Getter
public class ItemManagerContainer {
    private final ResourceManager resourceManager;
    private final BuildingManager buildingManager;
    private final ShipManager shipManager;
    private final OfficerManager officerManager;
    private final ResearchManager researchManager;
    private final MissionManager missionManager;

    public Resource getResourceFromStfcSpaceId(final long stfcSpaceId) {
        return getResourceManager().getResourceFromStfcSpaceId(stfcSpaceId);
    }

    public Resource getResource(final long resourceId) {
        return getResourceManager().getResource(resourceId);
    }

    public Building getBuilding(final long buildingId) {
        return getBuildingManager().getBuilding(buildingId);
    }

    public ShipClass getShipClassFromStfcSpaceId(final long stfcSpaceId) {
        return getShipManager().getShipClassFromStfcSpaceId(stfcSpaceId);
    }

    public ShipClass getShipClass(final long shipClassId) {
        return getShipManager().getShipClass(shipClassId);
    }

    public ShipClass getShipClass(final String name) {
        return getShipManager().getShipClassFromName(name);
    }

    public Officer getOfficerFromStfcSpaceId(final long stfcSpaceId) {
        return getOfficerManager().getOfficerFromStfcSpaceId(stfcSpaceId);
    }

    public Officer getOfficer(final long officerId) {
        return getOfficerManager().getOfficer(officerId);
    }

    public Research getResearch(final long researchId) {
        return getResearchManager().getResearch(researchId);
    }

    public Research getResearchFromStfcSpaceId(final long researchId) {
        return getResearchManager().getResearchFromStfcSpaceId(researchId);
    }

    public Mission getMission(final long missionId) {
        return getMissionManager().getMission(missionId);
    }
}