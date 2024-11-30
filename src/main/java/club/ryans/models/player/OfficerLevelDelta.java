package club.ryans.models.player;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
public class OfficerLevelDelta {
    private OfficerLevel previous;
    private OfficerLevel current;

    public long getOfficerId() {
        return current.getOfficerId();
    }
}
