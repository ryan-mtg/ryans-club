package club.ryans.models.dailies.tasks.resources;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class RewardSet implements Iterable<Reward> {
    private List<Reward> rewards = new ArrayList<>();

    @NotNull
    @Override
    public Iterator<Reward> iterator() {
        return rewards.iterator();
    }

    public void add(final Reward reward) {
        rewards.add(reward);
    }

    public RewardSet setChests(final int chests) {
        if (chests == 0) {
            return emptyRewards();
        }

        RewardSet result = new RewardSet();
        for (Reward reward : this) {
            result.add(reward.withChests(chests));
        }
        return result;
    }

    public static RewardSet emptyRewards() {
        return new RewardSet();
    }
}
