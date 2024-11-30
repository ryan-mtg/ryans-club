package club.ryans.models.player;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
public class ResearchLevelDelta {
    private ResearchLevel previous;
    private ResearchLevel current;

    public long getResearchId() {
        return current.getResearchId();
    }
}
