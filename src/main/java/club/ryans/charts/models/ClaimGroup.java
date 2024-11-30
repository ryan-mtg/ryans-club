package club.ryans.charts.models;

import club.ryans.models.accounting.ResourceAmount;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
public class ClaimGroup extends ColumnGroup {
    private Column capacity;
    private List<Column> rewards;
    private List<ResourceAmount> fallbackClaims;

    public int getNumberOfColumns() {
        return getCapacityColumns() + rewards.size() + getFallbackColumns();
    }

    public int getRewardStartIndex() {
        return getCapacityColumns();
    }

    public int getFallbackClaimsStartIndex() {
        return getCapacityColumns() + rewards.size();
    }

    private int getCapacityColumns() {
        return capacity == null || capacity.getValue() != null ? 0 : 1;
    }

    private int getFallbackColumns() {
        return fallbackClaims == null ? 0 : fallbackClaims.size();
    }
}