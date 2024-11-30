package club.ryans.models.dailies.descriptors;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class OpsClaimDailyDescriptor extends ChartClaimDailyDescriptor {
    @Override
    public boolean isOpsType() {
        return true;
    }
}
