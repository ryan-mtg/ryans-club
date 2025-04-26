package club.ryans.models;

import lombok.Data;

import java.time.Duration;
import java.util.List;

@Data
public class ShipComponent {
    private long id;
    private long stfcSpaceId;
    private int artId;

    private int order;

    private Duration buildTime;
    private Duration repairTime;

    private List<Cost> buildCosts;
}
