package club.ryans.stfcspace.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class ResearchLevel {
    private long id;

    @JsonProperty("strength")
    private int strength;

    @JsonProperty("strength_increase")
    private int strengthIncrease;

    @JsonProperty("research_time_in_seconds")
    private int researchTimeSeconds;

    @JsonProperty("hard_currency_cost")
    private long latinumCost;

    private List<Cost> costs;
    private List<Requirement> requirements;

    private List<Reward> rewards;
}
