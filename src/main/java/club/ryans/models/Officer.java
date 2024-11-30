package club.ryans.models;

import club.ryans.models.managers.AssetManager;
import lombok.Data;

@Data
public class Officer {
    private long id;
    private long stfcSpaceId;
    private String name;
    private String shortName;

    private int artId;
    private String artPath;

    private long factionId;
    private Rarity rarity;

    private String divisionName;
    private String synergyGroup;
    private int maxRank;

    private OfficerAbility captainAbility;
    private OfficerAbility officerAbility;
    private OfficerAbility belowDecksAbility;

    public String getImageUrl() {
        return AssetManager.makeUrl(artPath);
    }

    public String style() {
        return rarity.toStyle();
    }
}
