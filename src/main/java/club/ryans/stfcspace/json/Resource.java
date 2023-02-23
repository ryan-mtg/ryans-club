package club.ryans.stfcspace.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Resource {
    private long id;

    private int grade;

    private int rarity;

    @JsonProperty("art_id")
    private int artId;

    @JsonProperty("sorting_index")
    private int sortingIndex;
}
