package club.ryans.models.dailies.descriptors;

import club.ryans.models.ShipClass;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ShipClaimDailyDescriptor extends ChartClaimDailyDescriptor {
    private ShipClass shipClass;

    @Override
    public boolean isShipType() {
        return true;
    }
}
