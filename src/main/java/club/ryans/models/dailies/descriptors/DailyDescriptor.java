package club.ryans.models.dailies.descriptors;

import lombok.Getter;
import lombok.Setter;

import java.time.Duration;

@Getter @Setter
public abstract class DailyDescriptor {
    private String name;
    private String id;
    private Duration cooldown;

    public boolean isChart() {
        return false;
    }

    public boolean isInline() {
        return false;
    }

    public boolean isRedeem() {
        return false;
    }

    public boolean isClaim() {
        return false;
    }

    public boolean isNullType() {
        return false;
    }

    public boolean isOpsType() {
        return false;
    }

    public boolean isShipType() {
        return false;
    }

    public boolean isResearchType() {
        return false;
    }

    public boolean isBuildingType() {
        return false;
    }
}
