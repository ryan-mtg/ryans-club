package club.ryans.models.dailies.tasks.selectors;

import club.ryans.charts.models.BuildingChart;
import club.ryans.charts.models.Chart;
import club.ryans.models.Building;
import club.ryans.models.dailies.descriptors.BuildingClaimDailyDescriptor;
import club.ryans.models.dailies.descriptors.BuildingDailyDescriptor;
import club.ryans.models.dailies.descriptors.DailyDescriptor;
import club.ryans.models.player.PlayerItems;
import lombok.Data;

@Data
public class BuildingSelector extends Selector {
    private Building building;
    private int level;

    @Override
    public boolean isBuildingType() {
        return true;
    }

    @Override
    public void update(final DailyDescriptor dailyDescriptor, final Chart chart, final PlayerItems playerItems) {
        BuildingChart buildingChart = (BuildingChart) chart;

        if (dailyDescriptor instanceof BuildingDailyDescriptor) {
            building = ((BuildingDailyDescriptor) dailyDescriptor).getBuilding();
        } else if (dailyDescriptor instanceof BuildingClaimDailyDescriptor) {
            building = ((BuildingClaimDailyDescriptor) dailyDescriptor).getBuilding();
        }

        if (building == null) {
            building = buildingChart.getBuilding();
        }

        level = playerItems.getBuildingLevel(building.getId());
    }
}