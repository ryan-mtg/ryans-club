package club.ryans.models;

import club.ryans.models.managers.AssetManager;
import lombok.Data;

import java.util.List;

@Data
public class Building {
    public static final long OPS_ID = 0;

    private long id;
    private String name;
    private String description;

    private int maxLevel;
    private int unlockLevel;
    private int section;

    private String artPath;

    private List<BuildingLevel> levels;

    public String getImageUrl() {
        return AssetManager.makeUrl(artPath);
    }

    public String style() {
        return "rarity-common";
    }
}
