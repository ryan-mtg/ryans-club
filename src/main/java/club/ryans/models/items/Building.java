package club.ryans.models.items;

import club.ryans.models.BuildingLevel;
import club.ryans.models.assets.AssetType;
import lombok.Data;

import java.util.List;

@Data
public class Building extends Item {
    public static final long OPS_ID = 0;

    private int maxLevel;
    private int unlockLevel;
    private int section;

    private List<BuildingLevel> levels;

    public String style() {
        return "rarity-common";
    }

    @Override
    public void inflate(final Inflator inflator) {
        inflateArtPath(inflator, AssetType.BUILDING, (int) getId());

        if (levels != null) {
            for (BuildingLevel level : levels) {
                level.getCosts().forEach(inflator::inflateCost);
            }
        }
    }
}
