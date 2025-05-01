package club.ryans.models.items;

import club.ryans.models.Buff;
import club.ryans.models.Requirement;
import club.ryans.models.ResearchLevel;
import club.ryans.models.ResearchTree;
import club.ryans.models.ResearchType;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

@Data
public class Research extends Item {
    private int viewLevel;
    private int unlockLevel;
    private int maxLevel;

    private int row;
    private int column;
    private int generation;

    private ResearchTree researchTree;
    private ResearchType researchType;

    private List<ResearchLevel> levels;
    private List<Buff> buffs;

    public String style() {
        return "rarity-common";
    }

    public String getStfcSpaceUrl() {
        return Utility.stfcSpaceUrl("/researches/" + getStfcSpaceId());
    }

    public List<Requirement> getRequirements(final int index) {
        if (index == 0) {
            return levels.get(index).getRequirements();
        }
        List<Requirement> previousRequirements = levels.get(index - 1).getRequirements();
        return levels.get(index).getRequirements().stream()
                .filter(requirement -> !requirement.equalsAny(previousRequirements))
                .collect(Collectors.toList());
    }

    public String getBuffDescription(final int index) {
        return buffs.get(0).describe(index);
    }

    @Override
    public void inflate(final Inflator inflator) {
        if (levels != null) {
            levels.forEach(level -> inflateLevel(inflator, level));
        }
    }

    private void inflateLevel(final Inflator inflator, final ResearchLevel level) {
        level.getCosts().forEach(inflator::inflateCost);
    }
}