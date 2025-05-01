package club.ryans.models.calculators;

import club.ryans.models.items.Building;
import club.ryans.models.Cost;
import club.ryans.models.items.Resource;
import club.ryans.models.accounting.ResourceAmount;
import club.ryans.models.managers.BuildingManager;
import club.ryans.models.managers.ResourceManager;
import club.ryans.models.player.BuildingStats;
import club.ryans.models.player.PlayerItems;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class BuildingCalculator {
    private static final Map<Long, Long> MAIN_RESOURCE_MAP =
            new HashMap<Long, Long>() {{
                put(69L, 63014L); // Holodeck -> Holo-Field Amplifiers
                put(70L, 44005L); // Subspace Relay -> Subspace Relay Upgrade
                put(71L, 50100L); // Command Center -> Quantum Communicators
                put(72L, 52300L); // Mess Hall -> Replicator Rations
                put(73L, 54303L); // Artifact Gallery -> Artifact Gallery Schematics
                put(75L, 56102L); // Court of Q -> Emblem of Assessment
                put(77L, 61510L); // The Facade -> Section 31 Transmitter
                put(78L, 65501L); // The War Room -> Emblem of Honor
                put(79L, 68501L); // District 56 -> Mirror Fragments
                put(80L, 70003L); // Nova Squadron -> Starfleet Distinctions
                put(27L, 48050L); // Armory -> Nadion
                // put(81L, L); // Independent Archives -> B
            }};

    private static final Map<Long, Long> REVERSE_RESOURCE_MAP =
            new HashMap<Long, Long>() {{
                for (Map.Entry<Long, Long> entry : MAIN_RESOURCE_MAP.entrySet()) {
                    put(entry.getValue(), entry.getKey());
                }
            }};

    private final BuildingManager buildingManager;

    private final ResourceManager resourceManager;

    public Collection<Long> getBuildingResourceIds() {
        return MAIN_RESOURCE_MAP.values();
    }

    public BuildingStats computePlayerStats(final long resourceId, final PlayerItems playerItems) {
        if (!REVERSE_RESOURCE_MAP.containsKey(resourceId)) {
            return null;
        }

        final long buildingId = REVERSE_RESOURCE_MAP.get(resourceId);
        Building building = buildingManager.getBuilding(buildingId);
        return computePlayerStats(building, playerItems);
    }

    public BuildingStats computePlayerStats(final Building building, final PlayerItems playerItems) {
        BuildingStats buildingStats = new BuildingStats();
        buildingStats.setBuilding(building);

        int level = playerItems.getBuildingLevel(building.getId());
        buildingStats.setLevel(level);

        Resource mainResource = getMainResource(building);

        if (mainResource == null)  {
            buildingStats.setReachableLevel(level);
            return buildingStats;
        }

        ResourceAmount amountAvailable = playerItems.getResourceAmount(mainResource);
        buildingStats.setMainResourceAvailable(amountAvailable);

        computeReachableLevel(building, level, mainResource, amountAvailable, buildingStats);

        return buildingStats;
    }

    private Resource getMainResource(final Building building) {
        Long resourceId = MAIN_RESOURCE_MAP.get(building.getId());
        if (resourceId == null) {
            return null;
        }
        return resourceManager.getResource(resourceId);
    }

    private void computeReachableLevel(final Building building, final int currentLevel, final Resource mainResource,
            final ResourceAmount amountAvailable, final BuildingStats stats) {
        long spent = 0;

        int nextLevel = currentLevel + 1;
        StringBuilder log = new StringBuilder();

        log.append(String.format("Total available:  %d\n", amountAvailable.getAmount()));
        log.append(String.format("Current Level:  %d\n", currentLevel));

        while (nextLevel <= building.getLevels().size()) {
            List<Cost> costs = building.getLevels().get(nextLevel - 1).getCosts();
            log.append(String.format("Level %d\n", nextLevel));

            for (Cost cost : costs) {
                if (cost.getResourceId() == mainResource.getStfcSpaceId()) {
                    log.append(String.format("  >> cost = %d\n", cost.getAmount()));
                    if (spent + cost.getAmount() <= amountAvailable.getAmount()) {
                        log.append(String.format("  >> still more to go: spend = %d\n", spent));
                        spent += cost.getAmount();
                    } else {
                        log.append(String.format("  >> done! spend = %d\n", spent));
                        stats.setReachableLevel(nextLevel - 1);
                        stats.setLog(log.toString());
                        return;
                    }
                } else {
                    log.append(String.format("  >> cost.id = %d, main.id = %d\n", cost.getResourceId(), mainResource.getId()));
                }
            }

            nextLevel++;
        }

        stats.setReachableLevel(nextLevel - 1);
        stats.setLog(log.toString());
    }
}