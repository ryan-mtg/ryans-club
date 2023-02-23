package club.ryans.stfcspace.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class Level {
    private long id;

    @JsonProperty("player_strength")
    private int power;

    @JsonProperty("player_strength_increase")
    private int powerIncrease;

    @JsonProperty("strength")
    private int strength;

    @JsonProperty("strength_increase")
    private int strengthIncrease;

    @JsonProperty("build_time_in_seconds")
    private int buildTimeSeconds;

    @JsonProperty("hard_currency_cost")
    private long latinumCost;

    private List<Cost> costs;
    private List<Requirement> requirements;

    @JsonProperty("required_by")
    private List<Requirement> requiredBy;

    private List<Reward> rewards;
}
