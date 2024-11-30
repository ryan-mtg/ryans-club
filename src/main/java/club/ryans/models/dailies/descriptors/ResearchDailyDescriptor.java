package club.ryans.models.dailies.descriptors;

import club.ryans.models.Research;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ResearchDailyDescriptor extends ChartRedeemDailyDescriptor {
    private Research research;

    @Override
    public boolean isResearchType() {
        return true;
    }
}
