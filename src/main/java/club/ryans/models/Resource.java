package club.ryans.models;

import club.ryans.models.managers.AssetManager;
import lombok.Data;

@Data
public class Resource {
    private long id;
    private String name;
    private String shortName;
    private String description;

    private int artId;
    private String artPath;

    private int grade;
    private int rarity;

    public String getImageUrl() {
        return AssetManager.makeUrl(artPath);
    }

    public String style() {
        return "rarity-common";
    }
}
