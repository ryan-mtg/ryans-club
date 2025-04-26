package club.ryans.models;

import club.ryans.models.managers.AssetManager;
import lombok.Data;

import java.util.List;

@Data
public class ShipClass {
    private long id;
    private long stfcSpaceId;
    private String name;

    private int artId;
    private String artPath;

    private long factionId;
    private String rarity;

    private int maxTier;
    private int grade;
    private int scrapLevel;
    private int blueprintsRequired;
    private int hullType;
    private int maxLevel;

    private String blueprintName;
    private String description;
    private ShipBonus bonus = new ShipBonus();

    private List<ShipTier> tiers;

    public String getImageUrl() {
        return AssetManager.makeUrl(artPath);
    }

    public String style() {
        return String.format("rarity-%s", rarity.toLowerCase());
    }

    // private ??? buildCost;
    // private ??? buildRequirements;
}