package club.ryans.stfcspace.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Ship {
    private long id;

    @JsonProperty("loca_id")
    private int locaId;

    @JsonProperty("art_id")
    private int artId;

    private Faction faction;

    private String rarity;

    @JsonProperty("max_tier")
    private int maxTier;

    private int grade;

    @JsonProperty("scrap_level")
    private int scrapLevel;

    @JsonProperty("build_time_in_seconds")
    private int buildTime;

    @JsonProperty("blueprints_required")
    private int blueprintsRequired;

    @JsonProperty("hull_type")
    private int hullType;

    @JsonProperty("max_level")
    private int maxLevel;
}

/*
    "build_cost": [{ "resource_id": 743985951, "amount": 500}],
    "build_requirements": [],
 */
