package club.ryans.charts.dailies;

import club.ryans.models.ShipClass;
import lombok.Data;

@Data
public class ShipDailyTaskDescriptor extends DailyTaskDescriptor {
    private ShipClass shipClass;
}
