package club.ryans.models.dailies.tasks;

import club.ryans.models.Resource;
import club.ryans.models.accounting.ChestPrice;
import club.ryans.models.accounting.ResourceAmount;
import club.ryans.models.accounting.ResourceRequirement;
import club.ryans.models.dailies.tasks.resources.RewardSet;
import club.ryans.models.player.DailyConfiguration;
import club.ryans.models.player.PlayerItems;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class RedeemDailyTask extends DailyTask {
    public static enum State {
        READY,
        UNREADY,
    }

    private List<ChestPrice> prices;
    private int selectedChestIndex;

    private long redeems;
    private State state;
    private List<ResourceRequirement> required;
    private RewardSet rewards;

    @Override
    public boolean isRedeem() {
        return true;
    }

    public ChestPrice getSelectedChest() {
        return prices.get(selectedChestIndex);
    }

    public RewardSet getSelectedRewards() {
        int chests = prices.get(selectedChestIndex).getChests();
        return rewards.setChests(chests);
    }

    public void setSelection(final DailyConfiguration dailyConfiguration) {
        if (dailyConfiguration == null) {
            selectedChestIndex = prices.size() - 1;
            return;
        }

        for (int index = 0; index < prices.size(); index++) {
            if (prices.get(index).getChests() == dailyConfiguration.getChests()) {
                selectedChestIndex = index;
                return;
            }
        }

        selectedChestIndex = 0;
    }

    @Override
    public void computeRequirements(final PlayerItems playerItems) {
        ChestPrice chest = prices.get(selectedChestIndex);

        redeems = Long.MAX_VALUE;
        required = new ArrayList<>();

        for (ResourceAmount price : chest.getPrices()) {
            final Resource resource = price.getResource();
            long playerAmount = playerItems.getResourceAmount(resource).getAmount();

            ResourceRequirement requirement = new ResourceRequirement(resource, playerAmount, price.getAmount());
            redeems = Math.min(redeems, requirement.getRedeems());

            required.add(requirement);
        }

        state = (redeems > 0) ? State.READY : State.UNREADY;
    }
}