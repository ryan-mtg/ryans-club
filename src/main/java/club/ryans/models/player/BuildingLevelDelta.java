package club.ryans.models.player;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
public class BuildingLevelDelta {
    private BuildingLevel previous;
    private BuildingLevel current;

    public long getBuildingId() {
        return current.getBuildingId();
    }
}
