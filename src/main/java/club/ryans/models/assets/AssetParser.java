package club.ryans.models.assets;

import lombok.AllArgsConstructor;
import lombok.Getter;
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
    private static final Pattern ASSET_PATTERN = Pattern.compile("/assets/(\\d+)-([a-zA-Z0-9_-])+\\.png");

    private static final AssetClass[] assetClasses = {
            new AssetClass(AssetType.ABILITY, "0-DEE7gJlg.png", "99-BEqqo0PH.png"),      //**
            new AssetClass(AssetType.SHIP, "0-B4iAKGKF.png", "99-CQrzRRt7.png"),         //**
            new AssetClass(AssetType.REFIT, "10-B0Pyn1fn.png", "99-BFJekZkY.png"),       //**
            new AssetClass(AssetType.RESOURCE, "0-BT0hBcsB.png", "95-BZGxwALA.png"),     //**
            new AssetClass(AssetType.BUILDING, "0-BLZtRV--.png", "9-CLPEU1Sd.png"),      //**
            new AssetClass(AssetType.OFFICER, "1-BqZHzqHS.png", "999-Btub_Hmy.png"),     //**
            new AssetClass(AssetType.FORBIDDEN_TECH, "0-CUo7MrV8.png", "9-BB6bJK3J.png"),//**
            new AssetClass(AssetType.RESEARCH, "0-D_FOTUpt.png", "9-DvYL4aF0.png"),      //**
    };

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
        for (AssetClass assetClass : assetClasses) {
            if (url.equals(String.format("/assets/%s", assetClass.getFirst()))) {
               currentType = assetClass.getType();
               return;
            }
        }
    }

    private void postcheckAsset(final String url) {
        for (AssetClass assetClass : assetClasses) {
            if (url.equals(String.format("/assets/%s", assetClass.getLast()))) {
                currentType = AssetType.UNKNOWN;
                return;
            }
        }
    }

    private void addAsset(final Map<Integer, List<Asset>> assetMap, final int index, final String path) {
        if (assetMap.containsKey(index)) {
            List<Asset> existingAssets = assetMap.get(index);
            if (contains(existingAssets, path)) {
                LOGGER.trace("Collision! at {}, old value same as new", index);
            } else {
                existingAssets.add(new Asset(index, currentType, path));
                LOGGER.trace("Collision! at {}, new value: {}", index, path);
            }
        } else {
            LOGGER.trace("adding asset: {} -> {}", index, path);
            List<Asset> assets = new ArrayList<>();
            assets.add(new Asset(index, currentType, path));
            assetMap.put(index, assets);
        }
    }

    private boolean contains(final List<Asset> assets, final String path) {
        return assets.stream().anyMatch(asset -> asset.getPath().equals(path));
    }

    @Getter
    @AllArgsConstructor
    private static class AssetClass {
        private AssetType type;
        private String first;
        private String last;
    }
}