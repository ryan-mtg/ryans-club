package club.ryans.models.dailies.tasks;

import club.ryans.models.accounting.ResourceAmount;
import club.ryans.models.player.PlayerItems;
import lombok.Data;

import java.util.List;

@Data
public class ClaimDailyTask extends DailyTask {
    public static enum State {
        OVER_CAPACITY,
        UNDER_CAPACITY,
        NO_CAPACITY,
    }

    private ResourceAmount capacity;
    private long amount;
    private State state;

    private List<ResourceAmount> claims;

    @Override
    public boolean isClaim() {
        return true;
    }

    @Override
    public void computeRequirements(final PlayerItems playerItems) {
        if (capacity != null) {
            amount = playerItems.getResourceAmount(capacity.getResource()).getAmount();
            state = amount >= capacity.getAmount() ? State.OVER_CAPACITY : State.UNDER_CAPACITY;
        } else {
            state = State.NO_CAPACITY;
        }
    }

    public boolean hasNoCapacity() {
        return state == State.NO_CAPACITY;
    }

    public boolean isOverCapacity() {
        return state == State.OVER_CAPACITY;
    }

    public boolean isUnderCapacity() {
        return state == State.UNDER_CAPACITY;
    }

    public long getUsesRequired() {
        if (!isOverCapacity()) {
            return 0;
        }

        final long extra = amount - capacity.getAmount();
        return (extra + capacity.getAmount() - 1) / capacity.getAmount();
    }
}
