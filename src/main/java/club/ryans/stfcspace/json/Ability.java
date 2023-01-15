package club.ryans.stfcspace.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Ability {
    private long id;

    @JsonProperty("value_is_percentage")
    private boolean percentage;

    @JsonProperty("max_level")
    private int maxLevel;

    @JsonProperty("art_id")
    private int artId;

    private int flag;
}
