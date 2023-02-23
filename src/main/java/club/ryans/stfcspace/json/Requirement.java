package club.ryans.stfcspace.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Requirement {
    @JsonProperty("requirement_type")
    private String type;

    @JsonProperty("requirement_id")
    private long requirementId;

    @JsonProperty("requirement_level")
    private int requiredLevel;

    @JsonProperty("power_gain")
    private Integer powerGain;
}
