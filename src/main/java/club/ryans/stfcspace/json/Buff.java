package club.ryans.stfcspace.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class Buff {
    private long id;

    @JsonProperty("value_is_percentage")
    private boolean isPercentage;

    @JsonProperty("art_id")
    private int artId;

    @JsonProperty("show_percentage")
    private boolean showPercentage;

    @JsonProperty("value_type")
    private int valueType;

    private int flag;

    List<BuffValue> values;
}
