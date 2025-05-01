package club.ryans.charts.models;

import club.ryans.models.items.Building;
import club.ryans.models.player.PlayerItems;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class BuildingChart extends Chart {
    private Building building;

    @Override
    protected int selectRow(final PlayerItems playerItems) {
        int level = playerItems.getBuildingLevel(building.getId());
        if (level == 0) {
            return 10;
        }
        return level;
    }
}