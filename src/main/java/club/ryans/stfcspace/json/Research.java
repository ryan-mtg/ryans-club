package club.ryans.stfcspace.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Research {
        private long id;

        @JsonProperty("loca_id")
        private int locaId;

        @JsonProperty("art_id")
        private int artId;

        @JsonProperty("view_level")
        private int viewLevel;

        @JsonProperty("unlock_level")
        private int unlockLevel;

        @JsonProperty("max_level")
        private int maxLevel;

        @JsonProperty("research_tree")
        private ResearchTree researchTree;

        private int row;

        private int column;
}
