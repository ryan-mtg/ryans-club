package club.ryans.models.dailies.descriptors;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class OpsDailyDescriptor extends ChartRedeemDailyDescriptor {
    @Override
    public boolean isOpsType() {
        return true;
    }
}
