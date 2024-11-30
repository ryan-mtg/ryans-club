package club.ryans.models.dailies.descriptors;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public abstract class ChartRedeemDailyDescriptor extends ChartDailyDescriptor {
    private String chestGroupId;

    @Override
    public boolean isRedeem() {
        return true;
    }
}
