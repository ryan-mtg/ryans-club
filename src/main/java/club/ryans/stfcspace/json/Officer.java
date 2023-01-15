package club.ryans.stfcspace.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Officer {
    private long id;

    @JsonProperty("art_id")
    private int artId;

    private long faction;

    @JsonProperty("class")
    private int classId;

    private String rarity;

    @JsonProperty("synergy_id")
    private int synergyId;

    @JsonProperty("max_rank")
    private int maxRank;

    private Ability ability;

    @JsonProperty("captain_ability")
    private Ability captainAbility;

    @JsonProperty("below_decks_ability")
    private Ability belowDecksAbility;
}
