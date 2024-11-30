package club.ryans.stfcspace.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Hostile {
    private long id;

    @JsonProperty("loca_id")
    private int locaId;

    private Faction faction;
    private int level;

    @JsonProperty("ship_type")
    private int shipType;

    @JsonProperty("is_scout")
    private boolean scout;

    @JsonProperty("hull_type")
    private int hullType;

    private int rarity;
    private int count;
    private long strength;
    private int warp;

    @JsonProperty("warp_with_superhighway")
    private int warpWithSuperhighway;
}