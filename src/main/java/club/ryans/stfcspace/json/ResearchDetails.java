package club.ryans.stfcspace.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class ResearchDetails {
    private long id;

    @JsonProperty("loca_id")
    private int locaId;

    @JsonProperty("art_id")
    private int artId;

    @JsonProperty("view_level")
    private int viewLevel;

    @JsonProperty("research_tree")
    private ResearchTree researchTree;

    List<Buff> buffs;
    List<ResearchLevel> levels;

    private int row;
    private int column;
    private int generation;
}
