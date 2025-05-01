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
    private static final String ASSET_DOMAIN = "https://assets.stfc.space";
    private static final String PATH_TEMPLATE = "/thumbs/%s/i/%d.png";

    private Map<Integer, List<Asset>> assetMap = new HashMap<>();
    private final String thumbsVersion;

    public AssetManager(final String thumbsVersion) {
        this.thumbsVersion = thumbsVersion;

        try {
            String url = String.format("https://stfc.space/assets/thumbs-%s.js", thumbsVersion);
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

    public static String makeUrl(final String path) {
        return ASSET_DOMAIN + path;
    }

    public String getBuildingPath(final int artId) {
        return getAssetPath(AssetType.BUILDING, artId);
    }

    public String getResearchPath(final int artId) {
        return getAssetPath(AssetType.RESEARCH, artId);
    }

    public String getOfficerPath(final int artId) {
        return getAssetPath(AssetType.OFFICER, artId);
    }

    public String getResourcePath(final int artId) {
        return getAssetPath(AssetType.RESOURCE, artId);
    }

    public String getShipPath(final int artId) {
        return getAssetPath(AssetType.SHIP, artId);
    }

    public String getAssetPath(final AssetType type, final int artId) {
        switch (type) {
            case BUILDING:
                return String.format(PATH_TEMPLATE, "building", artId);
            case OFFICER:
                return String.format(PATH_TEMPLATE, "officer", artId);
            case RESOURCE:
                return String.format(PATH_TEMPLATE, "resource", artId);
            case SHIP:
                return String.format(PATH_TEMPLATE, "ship", artId);
            case RESEARCH:
                return String.format(PATH_TEMPLATE, "research", artId);
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

    private String makeAssetUrl(final AssetType type, final int artId) {
        return makeUrl(getAssetPath(type, artId));
    }

    private void parse(final InputStream inputStream) {
        assetMap = ASSET_PARSER.parse(inputStream);
    }
}