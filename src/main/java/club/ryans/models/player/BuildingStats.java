package club.ryans.models.player;

import club.ryans.models.items.Building;
import club.ryans.models.accounting.ResourceAmount;
import lombok.Data;

@Data
public class BuildingStats {
    private Building building;
    private int level;
    private ResourceAmount mainResourceAvailable;

    private int reachableLevel;
    private ResourceAmount mainResourceNeeded;

    private String log;
}
