package club.ryans.stfcspace.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Building {
    private long id;

    @JsonProperty("max_level")
    private int maxLevel;

    @JsonProperty("unlock_level")
    private int unlockLevel;

    private int section;
}
