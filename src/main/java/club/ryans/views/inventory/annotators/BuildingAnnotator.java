package club.ryans.views.inventory.annotators;

import club.ryans.models.calculators.BuildingCalculator;
import club.ryans.models.player.BuildingStats;
import club.ryans.models.player.PlayerItems;
import club.ryans.views.inventory.InventoryItem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Component
@RequiredArgsConstructor
public class BuildingAnnotator implements InventoryAnnotator {
    private final BuildingCalculator buildingCalculator;

    @Override
    public List<String> getAnnotations(final InventoryItem item, final PlayerItems playerItems) {
        BuildingStats stats = buildingCalculator.computePlayerStats(item.getResourceId(), playerItems);

        List<String> annotations = new ArrayList<>();

        if (stats.getLevel() != 0) {
            annotations.add(String.format("Level %d", stats.getLevel()));
        }

        if (stats.getReachableLevel() != 0 && stats.getReachableLevel() != stats.getLevel()) {
            annotations.add(String.format("Can reach %d", stats.getReachableLevel()));
        }

        if (stats.getMainResourceNeeded() != null) {
            annotations.add(String.format("Need %d more to reach level %d", stats.getMainResourceNeeded().getAmount(),
                    stats.getReachableLevel() + 1));
        }

        return annotations;
    }

    @Override
    public Collection<Long> getApplicableResourceIds() {
        return buildingCalculator.getBuildingResourceIds();
    }
}