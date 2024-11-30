package club.ryans.charts.ex.borg;

import club.ryans.charts.models.OpsChart;
import club.ryans.charts.parser.BundleParser;
import club.ryans.utility.parser.FieldParser;
import club.ryans.charts.parser.ParserFactory;
import club.ryans.utility.parser.StructParser;
import lombok.Getter;

import java.util.List;

public class ExBorgEfficiencyCalculator {
    private static final String EX_BORG_FACTION_RANKS_RESOURCE_FILE = "data/charts/ex-borg-faction-ranks.txt";
    private static final String XINDI_SCRAPS_CHART_RESOURCE_FILE = "data/charts/xindi-scrap-chart.txt";

    @Getter
    private final List<EfficiencyBundle> bundles;

    @Getter
    private final List<CreditSource> sources;

    @Getter
    private final List<FactionRank> ranks;

    @Getter
    private final OpsChart scrapsChart;

    public ExBorgEfficiencyCalculator(final BundleParser bundleParser, final ParserFactory parserFactory) {
        this.bundles = bundleParser.getBundles();
        this.sources = null;


        StructParser<FactionRank> rankParser = createRankParser();
        this.ranks = rankParser.parseList(EX_BORG_FACTION_RANKS_RESOURCE_FILE);

        StructParser<OpsChart> scrapsParser = parserFactory.createOpsChartParser();
        this.scrapsChart = scrapsParser.parseSingle(XINDI_SCRAPS_CHART_RESOURCE_FILE);
    }

    private StructParser<FactionRank> createRankParser() {
        return StructParser.createStructParser(FactionRank.class, "rank",
            FieldParser.stringField("name", FactionRank::setName),
            FieldParser.integerField("index", FactionRank::setIndex),
            FieldParser.rangeField("reputation", FactionRank::setReputation));
    }
}
