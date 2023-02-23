package club.ryans.stfcspace.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Reward {
    @JsonProperty("resource_id")
    private long resourceId;

    private int type;
    private int amount;
}
