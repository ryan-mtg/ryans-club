package club.ryans.stfcspace.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class Buff {
    private long id;

    @JsonProperty("loca_id")
    private int locaId;

    @JsonProperty("value_is_percentage")
    private boolean isPercentage;

    @JsonProperty("art_id")
    private int artId;

    @JsonProperty("show_percentage")
    private boolean showPercentage;

    @JsonProperty("value_type")
    private int valueType;

    @JsonProperty("ranked_is_value_chance")
    private double rankedIsValueChance;

    List<BuffValue> values;
}
