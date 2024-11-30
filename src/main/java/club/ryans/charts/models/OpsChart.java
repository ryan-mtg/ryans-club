package club.ryans.charts.models;

import club.ryans.models.player.PlayerItems;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class OpsChart extends Chart {

    @Override
    protected int selectRow(final PlayerItems playerItems) {
        return playerItems.getOpsLevel();
    }
}