package club.ryans.charts.models;

import club.ryans.models.Resource;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
public class ChestGroup extends ColumnGroup {
    private Resource costResource;
    private Resource rewardResource;
    private List<ChestDescriptor> costs;
    private List<Column> rewards;

    public int getNumberOfColumns() {
        return getRewardStartIndex() + rewards.size();
    }

    public int getRewardStartIndex() {
        int costsCount = 0;
        for (ChestDescriptor chestDescriptor : costs) {
            if (chestDescriptor.getValue() == null) {
                costsCount++;
            }
        }
        return costsCount;
    }
}