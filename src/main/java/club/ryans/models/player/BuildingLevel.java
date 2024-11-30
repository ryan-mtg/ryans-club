package club.ryans.models.player;

import club.ryans.models.Building;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
public class BuildingLevel {
    private Building building;
    private int level;

    public long getBuildingId() {
        return building.getId();
    }
}
