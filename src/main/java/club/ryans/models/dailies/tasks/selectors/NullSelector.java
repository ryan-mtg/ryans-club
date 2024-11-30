package club.ryans.models.dailies.tasks.selectors;

import club.ryans.charts.models.Chart;
import club.ryans.models.dailies.descriptors.DailyDescriptor;
import club.ryans.models.player.PlayerItems;

public class NullSelector extends Selector {
    @Override
    public void update(final DailyDescriptor dailyDescriptor, final Chart chart, final PlayerItems playerItems) {}

    @Override
    public boolean isNullType() {
        return true;
    }
}
