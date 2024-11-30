package club.ryans.models.managers;

import club.ryans.charts.models.BuildingChart;
import club.ryans.charts.models.Chart;
import club.ryans.charts.models.OpsChart;
import club.ryans.charts.models.ResearchChart;
import club.ryans.charts.models.ShipChart;
import club.ryans.charts.parser.ParserFactory;
import club.ryans.utility.Strings;
import club.ryans.utility.parser.StructParser;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

@Component
public class ChartManager {
    private static final String CHART_DIRECTORY = "data/charts";
    private static final String CHART_LIST_RESOURCE_FILE = CHART_DIRECTORY + "/chart-list.txt";

    private Map<String, Chart> chartMap = new HashMap<>();

    private StructParser<ShipChart> shipChartParser;
    private StructParser<OpsChart> opsChartParser;
    private StructParser<ResearchChart> researchChartParser;
    private StructParser<BuildingChart> buildingChartParser;

    public ChartManager(final ParserFactory parserFactory) {
        shipChartParser = parserFactory.createShipChartParser();
        opsChartParser = parserFactory.createOpsChartParser();
        researchChartParser = parserFactory.createResearchChartParser();
        buildingChartParser = parserFactory.createBuildingChartParser();

        ChartList chartList = ChartList.create(CHART_LIST_RESOURCE_FILE);

        for (String resourceFile : chartList.getOpsCharts()) {
            parseOpsChart(resourceFile);
        }

        for (String resourceFile : chartList.getShipCharts()) {
            parseShipChart(resourceFile);
        }

        for (String resourceFile : chartList.getResearchCharts()) {
            parseResearchChart(resourceFile);
        }

        for (String resourceFile : chartList.getBuildingCharts()) {
            parseBuildingChart(resourceFile);
        }
    }

    public Chart getChart(final String id) {
        return chartMap.get(id);
    }

    private void parseShipChart(final String resourceFile) {
        Chart chart = shipChartParser.parseSingle(String.format("%s/%s", CHART_DIRECTORY, resourceFile));
        addChart(chart);
    }

    private void parseOpsChart(final String resourceFile) {
        Chart chart = opsChartParser.parseSingle(String.format("%s/%s", CHART_DIRECTORY, resourceFile));
        addChart(chart);
    }

    private void parseResearchChart(final String resourceFile) {
        Chart chart = researchChartParser.parseSingle(String.format("%s/%s", CHART_DIRECTORY, resourceFile));
        addChart(chart);
    }

    private void parseBuildingChart(final String resourceFile) {
        Chart chart = buildingChartParser.parseSingle(String.format("%s/%s", CHART_DIRECTORY, resourceFile));
        addChart(chart);
    }

    private void addChart(final Chart chart) {
        final String chartId = chart.getId();
        if (chartMap.containsKey(chartId)) {
            throw new RuntimeException(String.format("duplicate chart id: %s", chartId));
        }
        chartMap.put(chartId, chart);
    }

    @Getter
    private static class ChartList {
        private List<String> opsCharts = new ArrayList<>();
        private List<String> shipCharts = new ArrayList<>();
        private List<String> researchCharts = new ArrayList<>();
        private List<String> buildingCharts = new ArrayList<>();

        private static ChartList create(final String resourceFile) {
            ChartList chartList = new ChartList();

            InputStream stream = chartList.getClass().getClassLoader().getResourceAsStream(resourceFile);
            Scanner scanner = new Scanner(stream);

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();

                if (Strings.isBlank(line) || line.startsWith("#")) {
                    continue;
                }

                String[] segments = line.split(",");

                switch (segments[0].trim()) {
                    case "ops_chart":
                        chartList.opsCharts.add(segments[1].trim());
                        break;
                    case "ship_chart":
                        chartList.shipCharts.add(segments[1].trim());
                        break;
                    case "research_chart":
                        chartList.researchCharts.add(segments[1].trim());
                        break;
                    case "building_chart":
                        chartList.buildingCharts.add(segments[1].trim());
                        break;
                }
            }

            return chartList;
        }
    }
}