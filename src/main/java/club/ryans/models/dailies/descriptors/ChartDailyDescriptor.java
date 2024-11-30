package club.ryans.models.dailies.descriptors;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public abstract class ChartDailyDescriptor extends DailyDescriptor {
    private String chartId;

    @Override
    public boolean isChart() {
        return true;
    }
}
