package club.ryans.models.items;

import club.ryans.models.assets.AssetType;
import club.ryans.models.managers.AssetManager;
import club.ryans.utility.Strings;
import lombok.Data;

@Data
public abstract class Item {
    private long id;
    private long stfcSpaceId;

    private String name;
    private String description;

    private int artId;
    private String artPath;

    public String getImageUrl() {
        return AssetManager.makeUrl(artPath);
    }

    public abstract void inflate(Inflator inflator);


    public void inflateArtPath(final Inflator inflator, final AssetType type, final int artId) {
        if (Strings.isBlank(getArtPath())) {
            setArtPath(inflator.getAssetPath(type, artId));
        }
    }
}
