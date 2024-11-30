package club.ryans.models;

import club.ryans.models.managers.AssetManager;
import club.ryans.stfcspace.json.ResearchTree;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class Research {
    private long id;
    private long stfcSpaceId;

    private String name;
    private String description;

    private int viewLevel;
    private int unlockLevel;
    private int maxLevel;

    private int row;
    private int column;

    private String artPath;
    private ResearchTree researchTree;

    public String getImageUrl() {
        return AssetManager.makeUrl(artPath);
    }

    public String style() {
        return "rarity-common";
    }
}
