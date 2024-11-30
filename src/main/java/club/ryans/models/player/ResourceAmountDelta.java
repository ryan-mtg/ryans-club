package club.ryans.models.player;

import club.ryans.models.accounting.ResourceAmount;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
public class ResourceAmountDelta {
    private ResourceAmount previous;
    private ResourceAmount current;

    public long getResourceId() {
        return current.getResourceId();
    }
}
