package club.ryans.models.dailies.descriptors;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ChartClaimDailyDescriptor extends ChartDailyDescriptor {
    private String claimGroupId;

    @Override
    public boolean isClaim() {
       return true;
    }
}