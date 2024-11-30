package club.ryans.models.accounting;

import club.ryans.models.Resource;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
public class ResourceRequirement {
    private Resource resource;
    private long onHand;
    private long cost;

    public long getResourceId() {
        return resource.getId();
    }

    public long getStillNeeded() {
        return Math.max(cost - onHand, 0);
    }

    public long getRedeems() {
        return onHand / cost;
    }

    public boolean isReady() {
        return onHand >= cost;
    }
}