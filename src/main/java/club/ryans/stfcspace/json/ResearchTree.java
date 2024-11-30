package club.ryans.stfcspace.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ResearchTree {
    private long id;

    @JsonProperty("loca_id")
    private int locaId;

    private int type;
}