package club.ryans.models.items;

import club.ryans.models.Cost;
import club.ryans.models.Officer;
import club.ryans.models.ShipClass;
import club.ryans.models.assets.AssetType;

public interface Inflator {
    Resource getResource(final long resourceId);
    Resource getResourceFromStfcSpaceId(final long stfcSpaceId);

    Building getBuilding(final long buildingId);

    ShipClass getShipClass(final long shipClassId);
    ShipClass getShipClassFromStfcSpaceId(final long stfcSpaceId);

    ShipClass getShipClass(final String name);

    Officer getOfficerFromStfcSpaceId(final long stfcSpaceId);
    Officer getOfficer(final long officerId);

    Research getResearch(final long researchId);
    Research getResearchFromStfcSpaceId(final long researchId);

    void inflateCost(Cost cost);

    String getAssetPath(AssetType type, int artId);
}