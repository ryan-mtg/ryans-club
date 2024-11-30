package club.ryans.stfcspace.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class System {
    private long id;

    private int level;
    private long faction;

    @JsonProperty("coords_x")
    private int x;

    @JsonProperty("coords_y")
    private int y;

    @JsonProperty("est_warp")
    private int warp;

    @JsonProperty("est_warp_with_superhighway")
    private int warpWithSuperhighway;

    @JsonProperty("is_deep_space")
    private boolean deepSpace;

    @JsonProperty("is_mirror_universe")
    private boolean mirrorUniverse;

    @JsonProperty("has_mines")
    private boolean mines;

    @JsonProperty("has_planets")
    private boolean planets;

    @JsonProperty("has_player_containers")
    private boolean housing;

    @JsonProperty("has_missions")
    private boolean missions;

    @JsonProperty("is_wave_defense")
    private boolean waveDefense;

    @JsonProperty("hazards_enabled")
    private boolean hazardsEnabled;

    @JsonProperty("mine_resources")
    private List<Long> mineResources;

    @JsonProperty("hostiles")
    private List<Hostile> hostiles;

    @JsonProperty("node_sizes")
    private List<Integer> nodes;
}