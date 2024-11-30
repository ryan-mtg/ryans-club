package club.ryans.models.dailies.tasks.selectors;

import club.ryans.charts.models.Chart;
import club.ryans.charts.models.ShipChart;
import club.ryans.models.ShipClass;
import club.ryans.models.dailies.descriptors.DailyDescriptor;
import club.ryans.models.dailies.descriptors.ShipClaimDailyDescriptor;
import club.ryans.models.dailies.descriptors.ShipDailyDescriptor;
import club.ryans.models.player.PlayerItems;
import club.ryans.models.player.Ship;
import lombok.Data;

@Data
public class ShipSelector extends Selector {
    private ShipClass shipClass;
    private Ship ship;
    private int tier;

    @Override
    public boolean isShipType() {
        return true;
    }

    @Override
    public void update(final DailyDescriptor dailyDescriptor, final Chart chart, final PlayerItems playerItems) {
        ShipChart shipChart = (ShipChart) chart;

        if (dailyDescriptor instanceof ShipDailyDescriptor) {
            shipClass = ((ShipDailyDescriptor) dailyDescriptor).getShipClass();
        } else if (dailyDescriptor instanceof ShipClaimDailyDescriptor) {
            shipClass = ((ShipClaimDailyDescriptor) dailyDescriptor).getShipClass();
        }

        if (shipClass == null) {
            shipClass = shipChart.getShipClass();
        }

        ship = playerItems.getShip(shipClass);
        tier = playerItems.getShipTier(shipClass);
    }
}