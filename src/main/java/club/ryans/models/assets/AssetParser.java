package club.ryans.models.assets;

import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class AssetParser {
    private static final Pattern ASSET_PATTERN = Pattern.compile("/assets/(\\d+)\\.([a-zA-Z0-9])+\\.png");

    private AssetType currentType;

    public Map<Integer, List<Asset>> parse(final InputStream inputStream) {
        currentType = AssetType.UNKNOWN;

        Scanner scanner = new Scanner(inputStream);
        scanner.useDelimiter("\"");


        Map<Integer, List<Asset>> assetMap = new HashMap<>();
        while(scanner.hasNext()) {
            if (scanner.hasNext(ASSET_PATTERN)) {
                String path = scanner.next(ASSET_PATTERN);
                Matcher matcher = ASSET_PATTERN.matcher(path);
                if (matcher.matches()) {
                    int index = Integer.parseInt(matcher.group(1));

                    precheckAsset(path);

                    addAsset(assetMap, index, path);

                    postcheckAsset(path);
                }
            } else {
                scanner.next();
            }
        }

        return assetMap;
    }

    private void precheckAsset(final String url) {
        if (url.equals("/assets/0.1e18755d.png")) {
            currentType = AssetType.SHIP;
        } else if (url.equals("/assets/1.6df9e1af.png")) {
            currentType = AssetType.OFFICER;
        }
    }

    private void postcheckAsset(final String url) {
        if (url.equals("/assets/99.7e35dbf3.png")) {
            currentType = AssetType.UNKNOWN;
        } else if (url.equals("/assets/999.17ff5135.png")) {
            currentType = AssetType.UNKNOWN;
        }
    }

    private void addAsset(final Map<Integer, List<Asset>> assetMap, final int index, final String path) {
        if (assetMap.containsKey(index)) {
            List<Asset> existingAssets = assetMap.get(index);
            if (contains(existingAssets, path)) {
                LOGGER.info("Collision! at {}, old value same as new", index);
            } else {
                existingAssets.add(new Asset(index, currentType, path));
                LOGGER.info("Collision! at {}, new value: {}", index, path);
            }
        } else {
            LOGGER.info("adding asset: {} -> {}", index, path);
            List<Asset> assets = new ArrayList<>();
            assets.add(new Asset(index, currentType, path));
            assetMap.put(index, assets);
        }
    }

    private boolean contains(final List<Asset> assets, final String path) {
        return assets.stream().anyMatch(asset -> asset.getPath().equals(path));
    }
}
