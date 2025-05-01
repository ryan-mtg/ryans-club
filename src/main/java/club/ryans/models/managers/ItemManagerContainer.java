package club.ryans.models.managers;

import club.ryans.models.Cost;
import club.ryans.models.assets.AssetType;
import club.ryans.models.items.Building;
import club.ryans.models.Mission;
import club.ryans.models.Officer;
import club.ryans.models.items.Research;
import club.ryans.models.items.Inflator;
import club.ryans.models.items.Resource;
import club.ryans.models.ShipClass;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@RequiredArgsConstructor
@Getter
public class ItemManagerContainer implements Inflator {
    private final ResourceManager resourceManager;
    private final BuildingManager buildingManager;
    private final ShipManager shipManager;
    private final OfficerManager officerManager;
    private final ResearchManager researchManager;
    private final MissionManager missionManager;
    private final AssetManager assetManager;

    @PostConstruct
    public void inflateManagers() {
        researchManager.inflate(this);
    }

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

    @Override
    public void inflateCost(final Cost cost) {
        if (cost.getResourceId() != 0 && cost.getResource() == null) {
            cost.setResource(getResourceFromStfcSpaceId(cost.getResourceId()));
        }
    }

    @Override
    public String getAssetPath(final AssetType type, final int artId) {
        return assetManager.getAssetPath(type, artId);
    }
}