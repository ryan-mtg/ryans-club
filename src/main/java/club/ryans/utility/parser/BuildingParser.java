package club.ryans.utility.parser;

import club.ryans.models.Building;
import club.ryans.models.managers.BuildingManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BuildingParser {
    private final BuildingManager buildingManager;

    public Building parseValue(final String text) {
        long buildingId = Long.parseLong(text);
        Building building = buildingManager.getBuilding(buildingId);
        if (building == null) {
            Parse.throwError("Unknown building: %d", buildingId);
        }
        return building;
    }
}