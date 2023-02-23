package club.ryans.stfcspace.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Cost {
    @JsonProperty("resource_id")
    private long resourceId;

    private long amount;
}
