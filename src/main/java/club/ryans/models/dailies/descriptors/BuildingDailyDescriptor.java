package club.ryans.models.dailies.descriptors;

import club.ryans.models.items.Building;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class BuildingDailyDescriptor extends ChartRedeemDailyDescriptor {
    private Building building;

    @Override
    public boolean isBuildingType() {
        return true;
    }
}
