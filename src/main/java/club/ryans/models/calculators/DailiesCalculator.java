package club.ryans.models.calculators;

import club.ryans.charts.models.Chart;
import club.ryans.charts.models.ChestDescriptor;
import club.ryans.charts.models.ChestGroup;
import club.ryans.charts.models.Column;
import club.ryans.charts.models.RowValue;
import club.ryans.models.Resource;
import club.ryans.models.accounting.ChestPrice;
import club.ryans.models.dailies.descriptors.ChartRedeemDailyDescriptor;
import club.ryans.models.dailies.descriptors.ChartClaimDailyDescriptor;
import club.ryans.models.dailies.descriptors.InlineClaimDailyDescriptor;
import club.ryans.models.dailies.descriptors.InlineRedeemDailyDescriptor;
import club.ryans.models.dailies.descriptors.DailyDescriptor;
import club.ryans.models.dailies.DailyGroupDescriptor;
import club.ryans.models.dailies.DailyGroupsDescriptor;
import club.ryans.models.dailies.DailySubgroupDescriptor;
import club.ryans.models.dailies.tasks.ClaimDailyTask;
import club.ryans.models.dailies.tasks.DailyTask;
import club.ryans.models.dailies.tasks.RedeemDailyTask;
import club.ryans.models.dailies.tasks.resources.Reward;
import club.ryans.models.dailies.tasks.resources.RewardSet;
import club.ryans.models.dailies.tasks.selectors.BuildingSelector;
import club.ryans.models.dailies.tasks.selectors.NullSelector;
import club.ryans.models.dailies.tasks.selectors.OpsSelector;
import club.ryans.models.dailies.tasks.selectors.ResearchSelector;
import club.ryans.models.dailies.tasks.selectors.Selector;
import club.ryans.models.dailies.tasks.selectors.ShipSelector;
import club.ryans.models.managers.ChartManager;
import club.ryans.models.managers.DailyDescriptorManager;
import club.ryans.models.player.DailyConfiguration;
import club.ryans.models.player.DailyConfigurations;
import club.ryans.models.player.PlayerItems;
import club.ryans.models.player.ResearchLevel;
import club.ryans.views.dailies.Dailies;
import club.ryans.views.dailies.DailyGroup;
import club.ryans.views.dailies.DailySubgroup;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class DailiesCalculator {
    private final ChartManager chartManager;
    private final DailyDescriptorManager dailyDescriptorManager;

    public DailiesCalculator(final ChartManager chartManager,
            final DailyDescriptorManager dailyDescriptorManager) {
        this.chartManager = chartManager;
        this.dailyDescriptorManager = dailyDescriptorManager;
    }

    public Dailies getDailies(final PlayerItems playerItems, final DailyConfigurations dailyConfigurations) {
        Dailies dailies = new Dailies();

        DailyGroupsDescriptor dailyGroupsDescriptor = dailyDescriptorManager.getDailyGroupsDescriptor();

        dailies.setGroups(dailyGroupsDescriptor.getGroups().stream()
            .map(groupDescriptor -> createDailyGroup(groupDescriptor, playerItems, dailyConfigurations))
            .collect(Collectors.toList()));

        setMainGroup(dailies);

        return dailies;
    }

    public DailyTask getDaily(final String dailyId, final PlayerItems playerItems,
            final DailyConfiguration dailyConfiguration) {
        DailyDescriptor dailyDescriptor = dailyDescriptorManager.getDailyDescriptor(dailyId);
        return createDaily(dailyDescriptor, playerItems, dailyConfiguration);
    }

    private void setMainGroup(final Dailies dailies) {
        boolean hasDefaultGroup = false;
        for (DailyGroup group : dailies.getGroups()) {
            if (group.isMain()) {
                dailies.setDefaultGroupId(group.getId());
                hasDefaultGroup = true;
                break;
            }
        }

        if (!hasDefaultGroup && !dailies.getGroups().isEmpty()) {
            DailyGroup firstGroup = dailies.getGroups().get(0);
            firstGroup.setMain(true);
            dailies.setDefaultGroupId(firstGroup.getId());
        }

        for (DailyGroup group : dailies.getGroups()) {
            group.setMain(dailies.getDefaultGroupId().equals(group.getId()));
        }
    }

    private DailyGroup createDailyGroup(final DailyGroupDescriptor groupDescriptor, final PlayerItems playerItems,
            final DailyConfigurations dailyConfigurations) {
        DailyGroup group = new DailyGroup();

        group.setName(groupDescriptor.getName());
        group.setId(groupDescriptor.getId());
        group.setMain(groupDescriptor.isMain());

        List<DailySubgroup> subgroups = groupDescriptor.getSubgroups().stream()
            .map(subgroupDescriptor -> createDailySubgroup(subgroupDescriptor, playerItems, dailyConfigurations))
            .filter(dailySubgroup -> !dailySubgroup.getDailies().isEmpty())
            .collect(Collectors.toList());
        group.setSubgroups(subgroups);
        return group;
    }

    private DailySubgroup createDailySubgroup(final DailySubgroupDescriptor subgroupDescriptor,
            final PlayerItems playerItems, final DailyConfigurations dailyConfigurations) {

        DailySubgroup subgroup = new DailySubgroup();

        subgroup.setName(subgroupDescriptor.getName());
        subgroup.setId(subgroupDescriptor.getId());
        subgroup.setShipClass(subgroupDescriptor.getShipClass());
        subgroup.setChartId(subgroupDescriptor.getChartId());

        List<DailyTask> dailies = subgroupDescriptor.getDailies().stream()
                .map(dailyDescriptor -> createDaily(dailyDescriptor, playerItems,
                    dailyConfigurations.getDaily(dailyDescriptor.getId())))
                .filter(dailyTask -> dailyTask != null)
                .collect(Collectors.toList());
        subgroup.setDailies(dailies);
        return subgroup;
    }

    private DailyTask createDaily(final DailyDescriptor dailyDescriptor, final PlayerItems playerItems,
            final DailyConfiguration dailyConfiguration) {
        Selector selector = createDailySelector(dailyDescriptor, playerItems);

        if (dailyDescriptor.isRedeem()) {
            return createRedeemDaily(selector, dailyDescriptor, playerItems, dailyConfiguration);
        }

        if (dailyDescriptor.isClaim()) {
            return createClaimDaily(selector, dailyDescriptor, playerItems, dailyConfiguration);
        }

        throw new RuntimeException("Daily is neither claim nor redeem: " + dailyDescriptor.getClass().getName());
    }

    private Selector createDailySelector(final DailyDescriptor dailyDescriptor, final PlayerItems playerItems) {
        if (dailyDescriptor.isShipType()) {
            return new ShipSelector();
        }

        if (dailyDescriptor.isOpsType()) {
            return new OpsSelector();
        }

        if (dailyDescriptor.isResearchType()) {
            return new ResearchSelector();
        }

        if (dailyDescriptor.isBuildingType()) {
            return new BuildingSelector();
        }

        if (dailyDescriptor.isNullType()) {
            return new NullSelector();
        }

        throw new RuntimeException("unknown type daily type: " + dailyDescriptor.getClass().getName());
    }

    private DailyTask createRedeemDaily(final Selector selector, final DailyDescriptor dailyDescriptor,
            final PlayerItems playerItems, final DailyConfiguration dailyConfiguration) {
        RedeemDailyTask daily = new RedeemDailyTask();
        daily.setSelector(selector);

        daily.setName(dailyDescriptor.getName());
        daily.setId(dailyDescriptor.getId());

        if (selector.isNullType()) {
            InlineRedeemDailyDescriptor redeemDailyDescriptor = (InlineRedeemDailyDescriptor) dailyDescriptor;

            List<ChestPrice> prices = makeChests(redeemDailyDescriptor.getChestGroup(), null, playerItems);
            daily.setPrices(prices);
            daily.setSelection(dailyConfiguration);
            daily.computeRequirements(playerItems);

            RewardSet rewards = makeRewards(redeemDailyDescriptor.getChestGroup(), null, playerItems);
            daily.setRewards(rewards);
        } else {
            ChartRedeemDailyDescriptor chartRedeemDailyDescriptor = (ChartRedeemDailyDescriptor) dailyDescriptor;
            final Chart chart = chartManager.getChart(chartRedeemDailyDescriptor.getChartId());
            daily.getSelector().update(dailyDescriptor, chart, playerItems);

            final String chestGroupId = chartRedeemDailyDescriptor.getChestGroupId();
            final ChestGroup chestGroup = chart.getChestGroup(chestGroupId);
            final List<RowValue> values = chart.getGroupValues(chestGroupId, playerItems);
            if (values == null) {
                return null;
            }

            List<ChestPrice> prices = makeChests(chestGroup, values, playerItems);
            daily.setPrices(prices);
            daily.setSelection(dailyConfiguration);
            daily.computeRequirements(playerItems);

            RewardSet rewards = makeRewards(chestGroup, values, playerItems);
            daily.setRewards(rewards);
        }

        return daily;
    }

    private DailyTask createClaimDaily(final Selector selector, final DailyDescriptor dailyDescriptor,
            final PlayerItems playerItems, final DailyConfiguration dailyConfiguration) {
        ClaimDailyTask daily = new ClaimDailyTask();
        daily.setSelector(selector);

        daily.setName(dailyDescriptor.getName());
        daily.setId(dailyDescriptor.getId());

        if (selector.isNullType()) {
            InlineClaimDailyDescriptor claimDailyDescriptor = (InlineClaimDailyDescriptor) dailyDescriptor;
            daily.setCapacity(claimDailyDescriptor.getCapacity());
            daily.computeRequirements(playerItems);

            daily.setClaims(claimDailyDescriptor.getClaims());
        } else {
            ChartClaimDailyDescriptor claimDailyDescriptor = (ChartClaimDailyDescriptor) dailyDescriptor;

            final Chart chart = chartManager.getChart(claimDailyDescriptor.getChartId());
            daily.getSelector().update(dailyDescriptor, chart, playerItems);

            final String claimGroupId = claimDailyDescriptor.getClaimGroupId();
            daily.setCapacity(chart.getCapacity(claimGroupId, playerItems));
            daily.computeRequirements(playerItems);

            daily.setClaims(chart.getClaims(claimGroupId, playerItems));
        }

        return daily;
    }

    private List<ChestPrice> makeChests(final ChestGroup chestGroup, final List<RowValue> values,
            final PlayerItems playerItems) {
        List<ChestPrice> prices = new ArrayList<>();

        if (chestGroup.getCosts().isEmpty()) {
            prices.add(ChestPrice.freeChest());
            return prices;
        }

        prices.add(ChestPrice.noChests());

        Resource costResource = chestGroup.getCostResource();
        int columnIndex = 0;
        for (ChestDescriptor chestDescriptor : chestGroup.getCosts()) {
            if (ResearchLevel.matches(chestDescriptor.getResearch(), playerItems)) {
                RowValue value = getValue(values, columnIndex, chestDescriptor.getValue());
                if (value != null && !value.isNull()) {
                    if (!value.isSingleValued()) {
                        throw new RuntimeException("Unexpected multi value for chest price: " + chestGroup.getId());
                    }
                    prices.add(ChestPrice.create(chestDescriptor.getChests(), costResource, value.getLong()));
                }
            }
            if (chestDescriptor.getValue() == null) {
                columnIndex++;
            }
        }
        return prices;
    }

    private RewardSet makeRewards(final ChestGroup chestGroup, final List<RowValue> values,
            final PlayerItems playerItems) {

        RewardSet rewards = new RewardSet();
        int columnIndex = chestGroup.getRewardStartIndex();

        for (Column column : chestGroup.getRewards()) {
            if (column.matches(playerItems)) {
                RowValue value = getValue(values, columnIndex, column.getValue());
                if (value != null && !value.isNull()) {
                    Reward reward = makeReward(chestGroup, column, value);
                    if (!reward.isEmpty()) {
                        rewards.add(reward);
                    }
                }
            }
            columnIndex++;
        }
        return rewards;
    }

    private Reward makeReward(final ChestGroup chestGroup, final Column column, final RowValue value) {
        Reward reward = new Reward();

        reward.setResource(column.getResource() == null ? chestGroup.getRewardResource() : column.getResource());

        if (value.isUnknown()) {
            reward.setUnknown(true);
            return reward;
        }

        reward.setChance(column.getChance() != null ? column.getChance()/100 : 1.0f);
        reward.setRolls(column.getRolls() != null ? column.getRolls() : 1);
        reward.setChests(1);

        reward.setValue(value);
        if (value.isMultivalued()) {
            reward.setProbabilities(column.getProbabilities());
        }

        return reward;
    }

    private RowValue getValue(final List<RowValue> values, final int index, final RowValue descriptorValue) {
        if (descriptorValue != null) {
            return descriptorValue;
        }
        return values != null ? values.get(index) : null;
    }
}