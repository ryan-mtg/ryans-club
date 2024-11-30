package club.ryans.models.dailies.tasks.selectors;

import club.ryans.charts.models.Chart;
import club.ryans.models.dailies.descriptors.DailyDescriptor;
import club.ryans.models.player.PlayerItems;

public abstract class Selector {
    public abstract void update(final DailyDescriptor dailyDescriptor, final Chart chart, final PlayerItems playerItems);

    public boolean isOpsType() {
        return false;
    }

    public boolean isShipType() {
        return false;
    }

    public boolean isResearchType() {
        return false;
    }

    public boolean isBuildingType() {
        return false;
    }

    public boolean isNullType() {
        return false;
    }
}
