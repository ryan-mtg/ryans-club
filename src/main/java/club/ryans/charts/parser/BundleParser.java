package club.ryans.charts.parser;

import club.ryans.charts.ex.borg.*;
import club.ryans.models.ShipClass;
import club.ryans.models.accounting.ChestPrice;
import club.ryans.models.accounting.ResourceAmount;
import club.ryans.models.managers.ResourceManager;
import club.ryans.models.managers.ShipManager;
import club.ryans.utility.parser.Parse;
import lombok.Getter;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class BundleParser {
    private static final String RESOURCE_FILE = "data/charts/ex-borg-efficiency.txt";

    @Getter
    final List<EfficiencyBundle> bundles;
    final ShipManager shipManager;
    final ResourceManager resourceManager;

    public BundleParser(final ShipManager shipManager, final ResourceManager resourceManager) {
        this.shipManager = shipManager;
        this.resourceManager = resourceManager;

        InputStream bundleStream = this.getClass().getClassLoader().getResourceAsStream(RESOURCE_FILE);
        Scanner scanner = new Scanner(bundleStream);
        bundles = parseBundles(scanner);
    }

    private List<EfficiencyBundle> parseBundles(final Scanner scanner) {
        List<EfficiencyBundle> bundles = new ArrayList<>();
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine().trim();
            if (line.isEmpty()) {
                continue;
            }

            if (!line.equals("ship {")) {
                Parse.throwError("unexpected bundle start: " + line);
            }
            bundles.add(parseBundle(scanner));
        }
        return bundles;
    }

    private EfficiencyBundle parseBundle(final Scanner scanner) {
        EfficiencyBundle bundle = new EfficiencyBundle();
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine().trim();

            if (line.charAt(0) == '}') {
                break;
            }

            int colonIndex = line.indexOf(':');
            if (colonIndex < 0) {
                Parse.throwError("unexpected bundle field: " + line);
            }

            String fieldName = line.substring(0, colonIndex).trim();

            switch (fieldName) {
                case "name":
                    int commaIndex = line.indexOf(',', colonIndex);
                    String name = line.substring(colonIndex + 1, commaIndex).trim();
                    ShipClass shipClass = shipManager.getShipClassFromName(name);
                    bundle.setShip(shipClass);
                    break;
                case "cost":
                    int endBraceIndex = line.indexOf('}', colonIndex);
                    String value = line.substring(colonIndex + 1, endBraceIndex + 1).trim();
                    bundle.setCosts(parseChestCosts(value));
                    break;
                case "tiers":
                    if (line.charAt(line.length() - 1) != '{') {
                        Parse.throwError("Expected the first line of tiers to end with {");
                    }
                    bundle.setTierRanges(parseTierRanges(scanner));
                    break;
                default:
                    Parse.throwError("Unknown bundle field name: " + fieldName);
            }
        }

        return bundle;
    }

    private List<ChestPrice> parseChestCosts(final String text) {
        validateBracketedText(text, '{', '}');

        List<ChestPrice> chestPrices = new ArrayList<>();

        int index = 1;
        int lastIndex = text.length() - 1;
        do {
            int endIndex = text.indexOf(']', index);
            if (endIndex < 0) {
                Parse.throwError("Chest cost does not have a terminator: " + text.substring(index));
            }

            endIndex++;
            chestPrices.add(parseChestCost(text.substring(index, endIndex)));

            if (text.charAt(endIndex) == ',') {
                endIndex++;
            }

            index = endIndex;
        } while(index < lastIndex);

        return chestPrices;
    }

    private ChestPrice parseChestCost(final String text) {
        int colonIndex = text.indexOf(':');
        if (colonIndex < 0) {
            Parse.throwError("No colon in chest cost: " + text);
        }

        int chests = Integer.parseInt(text.substring(0, colonIndex).trim());
        String costs = text.substring(colonIndex + 1).trim();

        ChestPrice chestPrice = ChestPrice.create(chests, parseResourceList(costs));

        return chestPrice;
    }

    private List<ResourceAmount> parseResourceList(final String text) {
        validateBracketedText(text, '[', ']');
        List<ResourceAmount> amounts = new ArrayList<>();
        for (String resourceText : text.substring(1, text.length() - 1).split(",")) {
            amounts.add(parseResourceAmount(resourceText.trim()));
        }
        return amounts;
    }

    private ResourceAmount parseResourceAmount(final String text) {
        Scanner scanner = new Scanner(text);
        if(!scanner.hasNextLong()) {
            Parse.throwError("Resource amount does not have amount: " + text);
        }

        long amount = scanner.nextLong();

        if(!scanner.hasNextInt()) {
            Parse.throwError("Resource amount does not a resource: " + text);
        }

        int resourceId = scanner.nextInt();

        ResourceAmount resourceAmount = new ResourceAmount(resourceManager.getResource(resourceId), amount);
        return resourceAmount;
    }

    private List<EfficiencyBundleTierRange> parseTierRanges(final Scanner scanner) {
        List<EfficiencyBundleTierRange> ranges = new ArrayList<>();
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine().trim();

            if (line.charAt(0) == '}') {
                break;
            }

            int colonIndex = line.indexOf(':');
            if (colonIndex < 0) {
                Parse.throwError("unexpected tier range: " + line);
            }

            String rangePart = line.substring(0, colonIndex).trim();
            EfficiencyBundleTierRange range = new EfficiencyBundleTierRange();

            range.setTiers(Parse.parseRange(rangePart));

            int endBracketIndex = line.indexOf(']', colonIndex);
            String rewards = line.substring(colonIndex + 1, endBracketIndex + 1).trim();
            range.setRewards(parseResourceList(rewards));
            ranges.add(range);
        }
        return ranges;
    }

    private void validateBracketedText(final String text, final char firstChar, final char lastChar) {
        if (text.charAt(0) != firstChar) {
            Parse.throwError("Unexpected first character: '" + text.charAt(0) + "' expecting " + firstChar);
        }

        int lastIndex = text.length() - 1;

        if (text.charAt(lastIndex) != lastChar) {
            Parse.throwError("Unexpected last character: '" + text.charAt(lastIndex) + "' expecting " + lastChar);
        }
    }
}