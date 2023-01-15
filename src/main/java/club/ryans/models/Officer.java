package club.ryans.models;

import club.ryans.models.managers.AssetManager;
import lombok.Data;

@Data
public class Officer {
    private long id;
    private String name;
    private String shortName;

    private int artId;
    private String artPath;

    private long factionId;
    private String rarity;

    private int classId;
    private int synergyId;
    private int maxRank;

    private OfficerAbility captainAbility;
    private OfficerAbility officerAbility;
    private OfficerAbility belowDecksAbility;

    public String getImageUrl() {
        return AssetManager.makeUrl(artPath);
    }

    public String style() {
        return String.format("rarity-%s", rarity.toLowerCase());
    }
}
