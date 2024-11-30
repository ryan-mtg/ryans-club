package club.ryans.models.managers;

import club.ryans.charts.models.ChestGroup;
import club.ryans.charts.parser.ParserFactory;
import club.ryans.models.accounting.ResourceAmount;
import club.ryans.models.dailies.DailyGroupDescriptor;
import club.ryans.models.dailies.DailyGroupsDescriptor;
import club.ryans.models.dailies.DailySubgroupDescriptor;
import club.ryans.models.dailies.descriptors.BuildingClaimDailyDescriptor;
import club.ryans.models.dailies.descriptors.BuildingDailyDescriptor;
import club.ryans.models.dailies.descriptors.ChartClaimDailyDescriptor;
import club.ryans.models.dailies.descriptors.ChartDailyDescriptor;
import club.ryans.models.dailies.descriptors.ChartRedeemDailyDescriptor;
import club.ryans.models.dailies.descriptors.DailyDescriptor;
import club.ryans.models.dailies.descriptors.InlineClaimDailyDescriptor;
import club.ryans.models.dailies.descriptors.InlineRedeemDailyDescriptor;
import club.ryans.models.dailies.descriptors.OpsClaimDailyDescriptor;
import club.ryans.models.dailies.descriptors.OpsDailyDescriptor;
import club.ryans.models.dailies.descriptors.ResearchDailyDescriptor;
import club.ryans.models.dailies.descriptors.ShipClaimDailyDescriptor;
import club.ryans.models.dailies.descriptors.ShipDailyDescriptor;
import club.ryans.utility.parser.BuildingParser;
import club.ryans.utility.parser.FieldParser;
import club.ryans.utility.parser.PolymorphicStructParser;
import club.ryans.utility.parser.ResearchParser;
import club.ryans.utility.parser.ResourceParser;
import club.ryans.utility.parser.ShipClassParser;
import club.ryans.utility.parser.StructParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class DailyDescriptorManager {
    private static final String DAILY_GROUPS_DESCRIPTION_FILE = "data/dailies/groups.txt";

    private final DailyGroupsDescriptor dailyGroupsDescriptor;
    private final ShipClassParser shipClassParser;

    private final BuildingParser buildingParser;
    private final ResourceParser resourceParser;
    private final ResearchParser researchParser;
    private final StructParser<ChestGroup> chestGroupParser;
    private final StructParser<ResourceAmount> resourceAmountParser;
    private final Map<String, DailyDescriptor> descriptorMap = new HashMap<>();

    public DailyDescriptorManager(final ChartManager chartManager, final ParserFactory parserFactory) {
        this.shipClassParser = parserFactory.getShipClassParser();
        this.resourceParser = parserFactory.getResourceParser();
        this.researchParser = parserFactory.getResearchParser();
        this.buildingParser = parserFactory.getBuildingParser();
        this.chestGroupParser = parserFactory.createChestGroupParser();
        this.resourceAmountParser = parserFactory.createResourceAmountParser();

        StructParser<DailyGroupsDescriptor> dailyGroupsParser = createDailyGroupsParser();
        dailyGroupsDescriptor = dailyGroupsParser.parseSingle(DAILY_GROUPS_DESCRIPTION_FILE);
        updateMap();
    }

    public DailyGroupsDescriptor getDailyGroupsDescriptor() {
        return dailyGroupsDescriptor;
    }

    public DailyDescriptor getDailyDescriptor(final String dailyId) {
        return descriptorMap.get(dailyId);
    }

    private void updateMap() {
        descriptorMap.clear();
        for (DailyGroupDescriptor dailyGroupDescriptor : dailyGroupsDescriptor.getGroups()) {
            for (DailySubgroupDescriptor dailySubgroupDescriptor : dailyGroupDescriptor.getSubgroups()) {
                for (DailyDescriptor dailyDescriptor : dailySubgroupDescriptor.getDailies()) {
                    descriptorMap.put(dailyDescriptor.getId(), dailyDescriptor);
                }
            }
        }
    }

    private StructParser<DailyGroupsDescriptor> createDailyGroupsParser() {
        StructParser<DailyGroupDescriptor> groupParser = createDailyGroupParser();

        return StructParser.createStructParser(DailyGroupsDescriptor.class, "dailies",
            FieldParser.structArrayField("groups", groupParser, DailyGroupsDescriptor::setGroups));
    }

    private StructParser<DailyGroupDescriptor> createDailyGroupParser() {
        StructParser<DailySubgroupDescriptor> subgroupParser = createDailySubgroupParser();

        return StructParser.createStructParser(DailyGroupDescriptor.class, "daily_group",
            FieldParser.stringField("name", DailyGroupDescriptor::setName),
            FieldParser.stringField("id", DailyGroupDescriptor::setId),
            FieldParser.booleanField("default", DailyGroupDescriptor::setMain),
            FieldParser.structArrayField("subgroups", subgroupParser, DailyGroupDescriptor::setSubgroups));
    }

    private StructParser<DailySubgroupDescriptor> createDailySubgroupParser() {
        PolymorphicStructParser<DailyDescriptor> dailyParser = createDailyParser();

        return StructParser.createStructParser(DailySubgroupDescriptor.class, "daily_subgroup",
            this::postProcessSubgroupDescriptor,
            FieldParser.stringField("name", DailySubgroupDescriptor::setName),
            FieldParser.stringField("id", DailySubgroupDescriptor::setId),
            FieldParser.shipClassField("ship", shipClassParser, DailySubgroupDescriptor::setShipClass),
            FieldParser.stringField("chartId", DailySubgroupDescriptor::setChartId),
            FieldParser.structArrayField("dailies", dailyParser, DailySubgroupDescriptor::setDailies));
    }

    private void postProcessSubgroupDescriptor(final DailySubgroupDescriptor subgroupDescriptor) {
        for (DailyDescriptor dailyDescriptor : subgroupDescriptor.getDailies()) {
            if (subgroupDescriptor.getChartId() != null && dailyDescriptor instanceof ChartDailyDescriptor) {
                ChartDailyDescriptor chartDailyDescriptor = (ChartDailyDescriptor) dailyDescriptor;
                if (chartDailyDescriptor.getChartId() == null) {
                    chartDailyDescriptor.setChartId(subgroupDescriptor.getChartId());
                }
            }

            if (subgroupDescriptor.getShipClass() != null && dailyDescriptor instanceof ShipDailyDescriptor) {
                ShipDailyDescriptor shipDailyDescriptor = (ShipDailyDescriptor) dailyDescriptor;
                if (shipDailyDescriptor.getShipClass() == null) {
                    shipDailyDescriptor.setShipClass(subgroupDescriptor.getShipClass());
                }
            }
        }
    }

    private PolymorphicStructParser<DailyDescriptor> createDailyParser() {
        return new PolymorphicStructParser<DailyDescriptor>(createShipRedeemDailyParser(),
            createShipClaimDailyParser(), createOpsRedeemDailyParser(), createOpsClaimDailyParser(),
            createResearchDailyParser(), createBuildingRedeemDailyParser(), createBuildingClaimDailyParser(),
            createInlineClaimDailyParser(), createInlineRedeemDailyParser());
    }

    private StructParser<OpsDailyDescriptor> createOpsRedeemDailyParser() {
        return StructParser.createStructParser(OpsDailyDescriptor.class, "ops_daily",
            FieldParser.stringField("name", DailyDescriptor::setName),
            FieldParser.stringField("id", DailyDescriptor::setId),
            FieldParser.durationField("cooldown", DailyDescriptor::setCooldown),
            FieldParser.stringField("chartId", ChartDailyDescriptor::setChartId),
            FieldParser.stringField("chestGroupId", ChartRedeemDailyDescriptor::setChestGroupId));
    }

    private StructParser<OpsClaimDailyDescriptor> createOpsClaimDailyParser() {
        return StructParser.createStructParser(OpsClaimDailyDescriptor.class, "ops_claim_daily",
            FieldParser.stringField("name", DailyDescriptor::setName),
            FieldParser.stringField("id", DailyDescriptor::setId),
            FieldParser.durationField("cooldown", DailyDescriptor::setCooldown),
            FieldParser.stringField("chartId", ChartDailyDescriptor::setChartId),
            FieldParser.stringField("claimGroupId", ChartClaimDailyDescriptor::setClaimGroupId));
    }

    private StructParser<InlineClaimDailyDescriptor> createInlineClaimDailyParser() {
        return StructParser.createStructParser(InlineClaimDailyDescriptor.class, "claim_daily",
            FieldParser.stringField("name", DailyDescriptor::setName),
            FieldParser.stringField("id", DailyDescriptor::setId),
            FieldParser.durationField("cooldown", DailyDescriptor::setCooldown),
            FieldParser.structField("capacity", resourceAmountParser, InlineClaimDailyDescriptor::setCapacity),
            FieldParser.structArrayField("claims", resourceAmountParser, InlineClaimDailyDescriptor::setClaims),
            FieldParser.structArrayField("fallback_claims", resourceAmountParser,
                InlineClaimDailyDescriptor::setFallbackClaims));
    }

    private StructParser<InlineRedeemDailyDescriptor> createInlineRedeemDailyParser() {
        return StructParser.createStructParser(InlineRedeemDailyDescriptor.class, "redeem_daily",
            FieldParser.stringField("name", DailyDescriptor::setName),
            FieldParser.stringField("id", DailyDescriptor::setId),
            FieldParser.durationField("cooldown", DailyDescriptor::setCooldown),
            FieldParser.structField("chests", chestGroupParser, InlineRedeemDailyDescriptor::setChestGroup));
    }

    private StructParser<ResearchDailyDescriptor> createResearchDailyParser() {
        return StructParser.createStructParser(ResearchDailyDescriptor.class, "research_daily",
            FieldParser.stringField("name", DailyDescriptor::setName),
            FieldParser.stringField("id", DailyDescriptor::setId),
            FieldParser.durationField("cooldown", DailyDescriptor::setCooldown),
            FieldParser.stringField("chestGroupId", ChartRedeemDailyDescriptor::setChestGroupId),
            FieldParser.stringField("chartId", ChartDailyDescriptor::setChartId),
            FieldParser.researchField("research", researchParser, ResearchDailyDescriptor::setResearch));
    }

    private StructParser<BuildingDailyDescriptor> createBuildingRedeemDailyParser() {
        return StructParser.createStructParser(BuildingDailyDescriptor.class, "building_daily",
            FieldParser.stringField("name", DailyDescriptor::setName),
            FieldParser.stringField("id", DailyDescriptor::setId),
            FieldParser.durationField("cooldown", DailyDescriptor::setCooldown),
            FieldParser.stringField("chestGroupId", ChartRedeemDailyDescriptor::setChestGroupId),
            FieldParser.stringField("chartId", ChartDailyDescriptor::setChartId),
            FieldParser.buildingField("building", buildingParser, BuildingDailyDescriptor::setBuilding));
    }

    private StructParser<BuildingClaimDailyDescriptor> createBuildingClaimDailyParser() {
        return StructParser.createStructParser(BuildingClaimDailyDescriptor.class, "building_claim_daily",
            FieldParser.stringField("name", DailyDescriptor::setName),
            FieldParser.stringField("id", DailyDescriptor::setId),
            FieldParser.durationField("cooldown", DailyDescriptor::setCooldown),
            FieldParser.stringField("chartId", ChartDailyDescriptor::setChartId),
            FieldParser.stringField("claimGroupId", ChartClaimDailyDescriptor::setClaimGroupId),
            FieldParser.buildingField("building", buildingParser, BuildingClaimDailyDescriptor::setBuilding));
    }

    private StructParser<ShipDailyDescriptor> createShipRedeemDailyParser() {
        return StructParser.createStructParser(ShipDailyDescriptor.class, "ship_daily",
            FieldParser.stringField("name", DailyDescriptor::setName),
            FieldParser.stringField("id", DailyDescriptor::setId),
            FieldParser.durationField("cooldown", DailyDescriptor::setCooldown),
            FieldParser.stringField("chestGroupId", ChartRedeemDailyDescriptor::setChestGroupId),
            FieldParser.stringField("chartId", ChartDailyDescriptor::setChartId),
            FieldParser.shipClassField("ship", shipClassParser, ShipDailyDescriptor::setShipClass));
    }

    private StructParser<ShipClaimDailyDescriptor> createShipClaimDailyParser() {
        return StructParser.createStructParser(ShipClaimDailyDescriptor.class, "ship_claim_daily",
            FieldParser.stringField("name", DailyDescriptor::setName),
            FieldParser.stringField("id", DailyDescriptor::setId),
            FieldParser.durationField("cooldown", DailyDescriptor::setCooldown),
            FieldParser.stringField("claimGroupId", ChartClaimDailyDescriptor::setClaimGroupId),
            FieldParser.stringField("chartId", ChartDailyDescriptor::setChartId),
            FieldParser.shipClassField("ship", shipClassParser, ShipClaimDailyDescriptor::setShipClass));
    }
}
