package club.ryans.charts.models;

import club.ryans.models.items.Resource;
import club.ryans.models.accounting.ResourceAmount;
import club.ryans.models.player.PlayerItems;
import lombok.Getter;
import lombok.Setter;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Getter @Setter
public abstract class Chart {
    private String title;
    private String id;
    private Duration cooldown;

    private List<ColumnGroup> columnGroups;

    private List<RangedRow> rows;

    public ChestGroup getChestGroup(final String chestGroupId) {
        for (ColumnGroup columnGroup : columnGroups) {
            if (matchesChestGroup(columnGroup, chestGroupId)) {
                return (ChestGroup) columnGroup;
            }

        }
        return null;
    }

    public ClaimGroup getClaimGroup(final String claimGroupId) {
        for (ColumnGroup columnGroup : columnGroups) {
            if (matchesClaimGroup(columnGroup, claimGroupId)) {
                return (ClaimGroup) columnGroup;
            }
        }
        return null;
    }

    public List<RowValue> getGroupValues(final String columnGroupId, final PlayerItems playerItems) {
        int rowIndex = selectRow(playerItems);
        RangedRow row = getRow(rowIndex);

        if (row == null) {
            return null;
        }

        int columnSkips = 0;
        for (ColumnGroup columnGroup : getColumnGroups()) {
            if (matchesColumnGroup(columnGroup, columnGroupId)) {
                return row.subarray(columnSkips, columnSkips + columnGroup.getNumberOfColumns());
            } else {
                columnSkips += columnGroup.getNumberOfColumns();
            }
        }

        return null;
    }

    public ResourceAmount getCapacity(final String claimGroupId, final PlayerItems playerItems) {
        int rowIndex = selectRow(playerItems);
        RangedRow row = getRow(rowIndex);

        if (row == null) {
            return null;
        }

        int columnSkips = 0;
        for (ColumnGroup columnGroup : getColumnGroups()) {
            if (matchesClaimGroup(columnGroup, claimGroupId)) {
                ClaimGroup claimGroup = (ClaimGroup) columnGroup;
                if (claimGroup.getCapacity() == null) {
                    return null;
                }
                return makeCapacity(claimGroup, row, columnSkips);
            } else {
                columnSkips += columnGroup.getNumberOfColumns();
            }
        }

        return null;
    }

    public List<ResourceAmount> getRewardAmounts(final String chestGroupId, final int chests,
            final PlayerItems playerItems) {
        int rowIndex = selectRow(playerItems);
        RangedRow row = getRow(rowIndex);

        int columnSkips = 0;
        for (ColumnGroup columnGroup : getColumnGroups()) {
            if (matchesChestGroup(columnGroup, chestGroupId)) {
                ChestGroup chestGroup = (ChestGroup) columnGroup;
                return getRewards(chestGroup, row, columnSkips, playerItems);
            } else {
                columnSkips += columnGroup.getNumberOfColumns();
            }
        }

        return null;
    }

    public List<ResourceAmount> getClaims(final String claimGroupId, final PlayerItems playerItems) {
        int rowIndex = selectRow(playerItems);
        RangedRow row = getRow(rowIndex);

        int columnSkips = 0;
        for (ColumnGroup columnGroup : getColumnGroups()) {
            if (matchesClaimGroup(columnGroup, claimGroupId)) {
                ClaimGroup claimGroup = (ClaimGroup) columnGroup;
                return getRewards(claimGroup, row, columnSkips, playerItems);
            } else {
                columnSkips += columnGroup.getNumberOfColumns();
            }
        }

        return null;
    }

    private RangedRow getRow(final int rowIndex) {
        for (RangedRow rangedRow : getRows()) {
            if (rangedRow.getRange().matches(rowIndex)) {
                return rangedRow;
            }
        }
        return null;
    }


    protected abstract int selectRow(final PlayerItems playerItems);

    private ResourceAmount makeCapacity(final ClaimGroup claimGroup, final RangedRow row, final int columnSkips) {
        RowValue rowValue = row.getValue(columnSkips);
        Resource resource = claimGroup.getCapacity().getResource() != null ? claimGroup.getCapacity().getResource()
                : claimGroup.getResource();
        return new ResourceAmount(resource, rowValue.getLong());
    }

    private List<ResourceAmount> getRewards(final ChestGroup chestGroup, final RangedRow row, final int columnSkips,
            final PlayerItems playerItems) {
        int columnIndex = columnSkips + chestGroup.getCosts().size();
        List<ResourceAmount> rewards = new ArrayList<>();
        for (Column column : chestGroup.getRewards()) {
            if (column.matches(playerItems)) {
                RowValue value = row.getValue(columnIndex);
                if (!value.isNull()) {
                    Resource resource = column.getResource() == null ?
                            chestGroup.getRewardResource() : column.getResource();
                    rewards.add(new ResourceAmount(resource, value.getLong()));
                }
            }
            columnIndex++;
        }
        return rewards;
    }

    private List<ResourceAmount> getRewards(final ClaimGroup claimGroup, final RangedRow row, final int columnSkips,
            final PlayerItems playerItems) {
        int columnIndex = columnSkips + claimGroup.getRewardStartIndex();
        List<ResourceAmount> claims = new ArrayList<>();
        for (Column column : claimGroup.getRewards()) {
            if (column.matches(playerItems)) {
                RowValue value = row.getValue(columnIndex);
                if (!value.isNull()) {
                    Resource resource = column.getResource() == null ?
                            claimGroup.getResource() : column.getResource();
                    claims.add(new ResourceAmount(resource, value.getLong()));
                }
            }
            columnIndex++;
        }
        return claims;
    }

    private boolean matchesColumnGroup(final ColumnGroup columnGroup, final String chestGroupId) {
        return chestGroupId.equals(columnGroup.getId());
    }

    private boolean matchesChestGroup(final ColumnGroup columnGroup, final String chestGroupId) {
        if (columnGroup instanceof ChestGroup) {
            ChestGroup chestGroup = (ChestGroup) columnGroup;
            if (chestGroupId.equals(chestGroup.getId())) {
                return true;
            }
        }
        return false;
    }

    private boolean matchesClaimGroup(final ColumnGroup columnGroup, final String chestGroupId) {
        if (columnGroup instanceof ClaimGroup) {
            ClaimGroup chestGroup = (ClaimGroup) columnGroup;
            if (chestGroupId.equals(chestGroup.getId())) {
                return true;
            }
        }
        return false;
    }
}