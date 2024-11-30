package club.ryans.charts.parser;

import club.ryans.charts.models.Chart;
import club.ryans.charts.models.BuildingChart;
import club.ryans.charts.models.ChestDescriptor;
import club.ryans.charts.models.ChestGroup;
import club.ryans.charts.models.ClaimGroup;
import club.ryans.charts.models.Column;
import club.ryans.charts.models.ColumnGroup;
import club.ryans.charts.models.OpsChart;
import club.ryans.charts.models.RangedRow;
import club.ryans.charts.models.ResearchChart;
import club.ryans.charts.models.RowValue;
import club.ryans.charts.models.ShipChart;
import club.ryans.models.accounting.ResourceAmount;
import club.ryans.utility.parser.BuildingParser;
import club.ryans.utility.parser.FieldParser;
import club.ryans.utility.parser.Parse;
import club.ryans.utility.parser.ResearchLevelParser;
import club.ryans.utility.parser.ResearchParser;
import club.ryans.utility.parser.ResourceParser;
import club.ryans.utility.parser.ShipClassParser;
import club.ryans.utility.parser.StructParser;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

@Component
@RequiredArgsConstructor
public class ParserFactory {
    @Getter
    private final ShipClassParser shipClassParser;
    @Getter
    private final ResourceParser resourceParser;
    @Getter
    private final ResearchParser researchParser;
    @Getter
    private final BuildingParser buildingParser;
    @Getter
    private final ResearchLevelParser researchLevelParser;

    private StructParser<ChestDescriptor> chestDescriptorParser;
    private StructParser<Column> columnParser;
    private StructParser<ColumnGroup> columnGroupParser;
    private StructParser<ChestGroup> chestGroupParser;
    private StructParser<ClaimGroup> claimGroupParser;
    private StructParser<ResourceAmount> resourceAmountParser;

    public StructParser<OpsChart> createOpsChartParser() {
        return StructParser.createStructParser(OpsChart.class, "ops_chart",
                FieldParser.stringField("title", OpsChart::setTitle),
                FieldParser.stringField("id", OpsChart::setId),
                FieldParser.durationField("cooldown", OpsChart::setCooldown),
                createFieldParser("columns", this::parseColumns, OpsChart::setColumnGroups),
                createFieldParser("rows", this::parseRangedRows, OpsChart::setRows));
    }

    public StructParser<ShipChart> createShipChartParser() {
        return StructParser.createStructParser(ShipChart.class, "chart",
                FieldParser.stringField("title", ShipChart::setTitle),
                FieldParser.stringField("id", ShipChart::setId),
                FieldParser.shipClassField("ship", shipClassParser, ShipChart::setShipClass),
                FieldParser.durationField("cooldown", ShipChart::setCooldown),
                createFieldParser("columns", this::parseColumns, ShipChart::setColumnGroups),
                createFieldParser("rows", this::parseRangedRows, ShipChart::setRows));
    }

    public StructParser<ResearchChart> createResearchChartParser() {
        return StructParser.createStructParser(ResearchChart.class, "research_chart",
                FieldParser.stringField("title", Chart::setTitle),
                FieldParser.stringField("id", Chart::setId),
                FieldParser.researchField("research", researchParser, ResearchChart::setResearch),
                FieldParser.durationField("cooldown", Chart::setCooldown),
                createFieldParser("columns", this::parseColumns, Chart::setColumnGroups),
                createFieldParser("rows", this::parseRangedRows, Chart::setRows));
    }

    public StructParser<BuildingChart> createBuildingChartParser() {
        return StructParser.createStructParser(BuildingChart.class, "building_chart",
                FieldParser.stringField("title", Chart::setTitle),
                FieldParser.stringField("id", Chart::setId),
                FieldParser.buildingField("building", buildingParser, BuildingChart::setBuilding),
                FieldParser.durationField("cooldown", Chart::setCooldown),
                createFieldParser("columns", this::parseColumns, Chart::setColumnGroups),
                createFieldParser("rows", this::parseRangedRows, Chart::setRows));
    }

    private StructParser<ChestDescriptor> createChestDescriptorParser() {
        if (chestDescriptorParser == null) {
            chestDescriptorParser = StructParser.createStructParser(ChestDescriptor.class, "chest",
                FieldParser.integerField("chests", ChestDescriptor::setChests),
                FieldParser.rowValueField("cost", ChestDescriptor::setValue),
                FieldParser.researchLevelArrayField("research", researchLevelParser, ChestDescriptor::setResearch));
        }

        return chestDescriptorParser;
    }

    public StructParser<Column> createColumnParser() {
        if (columnParser == null) {
            columnParser = StructParser.createStructParser(Column.class, "column",
                FieldParser.stringField("title", Column::setTitle),
                FieldParser.stringField("color", Column::setColor),
                FieldParser.stringField("background", Column::setBackground),
                FieldParser.resourceField("resource", resourceParser, Column::setResource),
                FieldParser.percentageArrayField("probabilities", Column::setProbabilities),
                FieldParser.percentageField("chance", Column::setChance),
                FieldParser.integerField("rolls", Column::setRolls),
                FieldParser.stringField("cellType", Column::setCellType),
                FieldParser.rowValueField("value", Column::setValue),
                FieldParser.researchLevelArrayField("research", researchLevelParser, Column::setResearch));
        }

        return columnParser;
    }

    public StructParser<ColumnGroup> createColumnGroupParser() {
        if (columnGroupParser == null) {
            StructParser<Column> columnParser = createColumnParser();

            columnGroupParser = StructParser.createStructParser(ColumnGroup.class, "column_group",
                    FieldParser.stringField("title", ColumnGroup::setTitle),
                    FieldParser.resourceField("resource", resourceParser, ColumnGroup::setResource),
                    FieldParser.stringField("color", Column::setColor),
                    FieldParser.stringField("background", Column::setBackground),
                    FieldParser.structArrayField("columns", columnParser, ColumnGroup::setColumns));
        }
        return columnGroupParser;
    }

    public StructParser<ChestGroup> createChestGroupParser() {
        if (chestGroupParser == null) {
            StructParser<Column> columnParser = createColumnParser();
            StructParser<ChestDescriptor> chestDescriptorParser = createChestDescriptorParser();

            chestGroupParser = StructParser.createStructParser(ChestGroup.class, "chest_group",
                    FieldParser.stringField("title", ChestGroup::setTitle),
                    FieldParser.stringField("id", ChestGroup::setId),
                    FieldParser.resourceField("costResource", resourceParser, ChestGroup::setCostResource),
                    FieldParser.resourceField("rewardResource", resourceParser, ChestGroup::setRewardResource),
                    FieldParser.stringField("color", ChestGroup::setColor),
                    FieldParser.stringField("background", ChestGroup::setBackground),
                    FieldParser.durationField("cooldown", ChestGroup::setCooldown),
                    FieldParser.structArrayField("columns", columnParser, ChestGroup::setColumns),
                    FieldParser.structArrayField("costs", chestDescriptorParser, ChestGroup::setCosts),
                    FieldParser.structArrayField("rewards", columnParser, ChestGroup::setRewards));
        }
        return chestGroupParser;
    }

    public StructParser<ClaimGroup> createClaimGroupParser() {
        if (claimGroupParser == null) {
            StructParser<Column> columnParser = createColumnParser();
            StructParser<ChestDescriptor> chestDescriptorParser = createChestDescriptorParser();
            StructParser<ResourceAmount> resourceAmountParser = createResourceAmountParser();

            claimGroupParser = StructParser.createStructParser(ClaimGroup.class, "claim_group",
                    FieldParser.stringField("title", ColumnGroup::setTitle),
                    FieldParser.stringField("id", ColumnGroup::setId),
                    FieldParser.resourceField("resource", resourceParser, ColumnGroup::setResource),
                    FieldParser.stringField("color", ColumnGroup::setColor),
                    FieldParser.stringField("background", ColumnGroup::setBackground),
                    FieldParser.durationField("cooldown", ColumnGroup::setCooldown),
                    FieldParser.structField("capacity", columnParser, ClaimGroup::setCapacity),
                    FieldParser.structArrayField("rewards", columnParser, ClaimGroup::setRewards),
                    FieldParser.structArrayField("fallback_claims", resourceAmountParser,
                            ClaimGroup::setFallbackClaims));
        }
        return claimGroupParser;
    }

    public StructParser<ResourceAmount> createResourceAmountParser() {
        if (resourceAmountParser == null) {
            resourceAmountParser = StructParser.createStructParser(ResourceAmount.class, "resource_amount",
                FieldParser.resourceField("resource", resourceParser, ResourceAmount::setResource),
                FieldParser.longField("amount", ResourceAmount::setAmount));

        }
        return resourceAmountParser;
    }

    private <StructType, FieldType> FieldParser<StructType, FieldType>  createFieldParser(final String name,
            final BiFunction<String, Scanner, FieldType> parser, final BiConsumer<StructType, FieldType> setter) {
        return new FieldParser<StructType, FieldType>(name, parser, setter);
    }

    private List<RangedRow> parseRangedRows(final String text, final Scanner scanner) {
        if (!text.equals("[")) {
            Parse.throwError("unexpected start of array: %s", text);
        }

        List<RangedRow> rows = new ArrayList<>();

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine().trim();
            if (line.isEmpty()) {
                continue;
            }

            if (line.startsWith("]")) {
                if (!"]".equals(line) && !"],".equals(line)) {
                    Parse.throwError("unexpected end of array: %s", line);
                }
                break;
            }

            if (line.endsWith(",")) {
                line = line.substring(0, line.length() - 1).trim();
            }

            rows.add(parseRow(line));
        }

        return rows;
    }

    private RangedRow parseRow(final String text) {
        RangedRow row = new RangedRow();

        if (!text.startsWith("[")) {
            Parse.throwError("unexpected start of array: %s", text);
        }

        if (!text.endsWith("]")) {
            Parse.throwError("unexpected end of array: %s", text);
        }

        String[] entries = text.substring(1, text.length() - 1).split(",");

        row.setRange(Parse.parseRange(entries[0].trim()));

        List<RowValue> values = new ArrayList<>();
        for (int i = 1; i < entries.length; i++) {
            values.add(Parse.parseRowValue(entries[i]));
        }
        row.setValues(values);

        return row;
    }

    private List<ColumnGroup> parseColumns(final String text, final Scanner scanner) {
        List<ColumnGroup> columnGroups = new ArrayList<>();

        StructParser<Column> columnParser = createColumnParser();
        StructParser<ColumnGroup> columnGroupParser = createColumnGroupParser();
        StructParser<ChestGroup> chestGroupParser = createChestGroupParser();
        StructParser<ClaimGroup> claimGroupParser = createClaimGroupParser();

        if (!text.equals("[")) {
            Parse.throwError("unexpected start of array: %s", text);
        }

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine().trim();
            if (line.isEmpty()) {
                continue;
            }

            if (line.startsWith("]")) {
                if (!"]".equals(line) && !"],".equals(line)) {
                    Parse.throwError("unexpected end of array: %s", line);
                }
                break;
            }

            if (!line.endsWith("{")) {
                Parse.throwError("unexpected column struct start: %s", line);
            }

            String structName = line.substring(0, line.length() - 1).trim();

            switch (structName) {
                case "column":
                    Column column = columnParser.parseStruct(scanner);
                    columnGroups.add(ColumnGroup.create(column));
                    break;
                case "column_group":
                    columnGroups.add(columnGroupParser.parseStruct(scanner));
                    break;
                case "chest_group":
                    columnGroups.add(chestGroupParser.parseStruct(scanner));
                    break;
                case "claim_group":
                    columnGroups.add(claimGroupParser.parseStruct(scanner));
                    break;
                default:
                    Parse.throwError("unexpected struct %s start: %s", structName, line);
            }
        }

        return columnGroups;
    }

    private RowValue parseRowValue(final String text) {
        if (text.equals("X")) {
            return RowValue.nullValue();
        }

        if (text.indexOf("?") >= 0) {
            return RowValue.unknownValue();
        }

        if (text.indexOf("/") > 0) {
            List<Long> values = new ArrayList<>();
            for (String value : text.split("/")) {
                values.add(Parse.parseScopelyValue(value));
            }

            return RowValue.createValue(values);
        }

        return RowValue.createValue(Parse.parseScopelyValue(text));
    }
}
