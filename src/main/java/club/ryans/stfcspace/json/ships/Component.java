package club.ryans.stfcspace.json.ships;

import club.ryans.stfcspace.json.Cost;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class Component {
    private long id;
    private int artId;

    @JsonProperty("loca_id")
    private int locaId;

    @JsonProperty("repair_time")
    private int repairTime;

    private int order;

    @JsonProperty("build_time_in_seconds")
    private int buildTime;

    @JsonProperty("build_cost")
    private List<Cost> buildCosts;
}
