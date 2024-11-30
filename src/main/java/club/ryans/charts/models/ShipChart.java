package club.ryans.charts.models;

import club.ryans.models.ShipClass;
import club.ryans.models.player.PlayerItems;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ShipChart extends Chart {
    private ShipClass shipClass;

    @Override
    protected int selectRow(final PlayerItems playerItems) {
        return playerItems.getShipTier(shipClass);
    }
}