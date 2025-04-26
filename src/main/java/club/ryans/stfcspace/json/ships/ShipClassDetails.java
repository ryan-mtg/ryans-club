package club.ryans.stfcspace.json.ships;

import club.ryans.stfcspace.json.Faction;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class ShipClassDetails {
    private long id;

    @JsonProperty("art_id")
    private int artId;

    @JsonProperty("loca_id")
    private int locaId;

    @JsonProperty("max_tier")
    private int maxTier;

    private String rarity;

    private int grade;

    @JsonProperty("scrap_level")
    private int scrapLevel;

    @JsonProperty("scrap_time_in_seconds")
    private long scrapTime;

    private Faction faction;

    @JsonProperty("blueprints_required")
    private long blueprintsRequired;

    @JsonProperty("hull_type")
    private int hullType;

    @JsonProperty("max_level")
    private int maxLevel;

    private List<ShipTier> tiers;

    //Build Requirements
    //Officer Bonus
    //Crew Slots
    //Levels
    //Abilities
    //Scrap
    //Base Scrap ???
    //Refits
    //asa??? activated ship ability
}
