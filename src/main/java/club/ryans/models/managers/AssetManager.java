package club.ryans.models.managers;

import club.ryans.models.assets.Asset;
import club.ryans.models.assets.AssetParser;
import club.ryans.models.assets.AssetType;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class AssetManager {
    private static final AssetParser ASSET_PARSER = new AssetParser();

    private Map<Integer, List<Asset>> assetMap = new HashMap<>();

    public AssetManager() {
        try {
            String url = "https://stfc.space/assets/thumbs.26c9a2bd.js";
            HttpClient httpClient = HttpClients.createDefault();
            HttpGet httpGet = new HttpGet(url);
            HttpResponse response = httpClient.execute(httpGet);
            parse(response.getEntity().getContent());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Map<Integer, List<Asset>> getAssetMap() {
        return assetMap;
    }

    public String getPath(final int artId) {
        List<Asset> assets = assetMap.get(artId);
        if (assets == null) {
            return "";
        }

        return assets.get(assets.size() - 1).getPath();
    }

    /*
    public String makeUrl(final Asset asset)  {
        return makeAssetUrl(asset.getId(), asset.getType());
    }
     */

    public static String makeUrl(final String path) {
        return "https://stfc.space" + path;
    }

    public String getBuildingPath(final int artId) {
        return getAssetPath(artId, AssetType.BUILDING);
    }

    public String getOfficerPath(final int artId) {
        return getAssetPath(artId, AssetType.OFFICER);
    }

    public String getResourcePath(final int artId) {
        return getAssetPath(artId, AssetType.RESOURCE);
    }

    public String getShipPath(final int artId) {
        return getAssetPath(artId, AssetType.SHIP);
    }

    private String makeAssetUrl(final int artId, final AssetType type) {
        return makeUrl(getAssetPath(artId, type));
    }

    private String getAssetPath(final int artId, final AssetType type) {
        if (artId == 82 && type == AssetType.SHIP) {
            return getAssetPath(30, type); // fix for stfc.space's HEG'TA bug
        }

        List<Asset> assets = assetMap.get(artId);
        if (assets == null) {
            return "";
        }

        for (Asset asset : assets) {
            if (asset.getType() == type) {
                return asset.getPath();
            }
        }

        return getPath(artId);
    }

    private void parse(final InputStream inputStream) {
        assetMap = ASSET_PARSER.parse(inputStream);
    }
}