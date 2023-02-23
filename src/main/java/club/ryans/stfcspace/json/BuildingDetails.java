package club.ryans.stfcspace.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class BuildingDetails {
    private long id;

    List<Level> levels;
    List<Buff> buffs;

    @JsonProperty("unlock_level")
    private int unlockLevel;

    private int section;
}
