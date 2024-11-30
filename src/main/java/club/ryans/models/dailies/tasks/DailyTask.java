package club.ryans.models.dailies.tasks;

import club.ryans.models.dailies.tasks.selectors.Selector;
import club.ryans.models.player.PlayerItems;
import lombok.Data;

@Data
public abstract class DailyTask {
    private String name;
    private String id;
    private Selector selector;

    public boolean isRedeem() {
        return false;
    }

    public boolean isClaim() {
        return false;
    }

    public boolean isOpsType() {
        return selector.isOpsType();
    }

    public boolean isShipType() {
        return selector.isShipType();
    }

    public boolean isResearchType() {
        return selector.isResearchType();
    }

    public boolean isBuildingType() {
        return selector.isBuildingType();
    }

    public abstract void computeRequirements(final PlayerItems playerItems);
}