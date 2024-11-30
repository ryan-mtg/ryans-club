package club.ryans.stfcspace.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Faction {
    private long id;

    @JsonProperty("loca_id")
    private long locaId;
}