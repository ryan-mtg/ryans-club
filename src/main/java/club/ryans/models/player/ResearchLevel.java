package club.ryans.models.player;

import club.ryans.models.items.Research;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
@AllArgsConstructor
public class ResearchLevel {
    private Research research;
    private int level;

    public long getResearchId() {
        // TODO: Remove this when research is set up
        if (research == null) {
            return 0;
        }
        return research.getId();
    }

    public static boolean matches(final List<ResearchLevel> research, final PlayerItems playerItems) {
        if (research == null) {
            return true;
        }

        for (ResearchLevel researchLevel : research) {
            if (playerItems.getResearchLevel(researchLevel.getResearchId()) != researchLevel.getLevel()) {
                return false;
            }
        }

        return true;
    }
}
