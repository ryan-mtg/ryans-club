package club.ryans.models;

import club.ryans.models.managers.AssetManager;
import lombok.Data;

import java.util.List;

@Data
public class Building {
    private long id;
    private String name;
    private String description;

    private int maxLevel;
    private int unlockLevel;
    private int section;

    private String artPath;

    private List<Level> levels;

    public String getImageUrl() {
        return AssetManager.makeUrl(artPath);
    }

    public String style() {
        return "rarity-common";
    }
}
