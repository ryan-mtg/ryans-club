package club.ryans.stfcspace.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class Mission {
    private long id;

    private long faction;

    @JsonProperty("recommeded_level")
    private int recommendedLevel;

    @JsonProperty("pickup_systems")
    private List<Long> pickupSystems;

    private int warp;

    @JsonProperty("warp_with_superhighway")
    private int warpWithSuperhighway;

    @JsonProperty("warp_for_completion")
    private int warpForCompletion;

    @JsonProperty("warp_for_completion_with_superhighway")
    private int warpForCompletionWithSuperhighway;
}