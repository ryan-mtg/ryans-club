package club.ryans.models.dailies.tasks.selectors;

import club.ryans.charts.models.Chart;
import club.ryans.models.dailies.descriptors.DailyDescriptor;
import club.ryans.models.player.PlayerItems;
import lombok.Data;

@Data
public class OpsSelector extends Selector {
    private int opsLevel;

    @Override
    public boolean isOpsType() {
        return true;
    }

    @Override
    public void update(final DailyDescriptor dailyDescriptor, final Chart chart, final PlayerItems playerItems) {
        opsLevel = playerItems.getOpsLevel();
    }
}