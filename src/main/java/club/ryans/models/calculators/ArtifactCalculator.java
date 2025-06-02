package club.ryans.models.calculators;

import club.ryans.models.ArtifactStats;
import club.ryans.models.Cost;
import club.ryans.models.ResearchLevel;
import club.ryans.models.ResearchType;
import club.ryans.models.accounting.ResourceAmount;
import club.ryans.models.items.Research;
import club.ryans.models.items.Resource;
import club.ryans.models.managers.ResearchManager;
import club.ryans.models.managers.ResourceManager;
import club.ryans.models.player.PlayerItems;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class ArtifactCalculator {

    private final ResearchManager researchManager;

    private final ResourceManager resourceManager;

    private Map<Long, Long> resourceMap = new HashMap<>();
    private Map<Long, Long> reverseResourceMap = new HashMap<>();

    public Collection<Long> getArtifactResourceIds() {
        return resourceMap.values();
    }

    public ArtifactStats computePlayerStats(final long resourceId, final PlayerItems playerItems) {
        if (!reverseResourceMap.containsKey(resourceId)) {
            return null;
        }

        final long artifactId = reverseResourceMap.get(resourceId);
        Research artifact = researchManager.getResearch(artifactId);
        return computePlayerStats(artifact, playerItems);
    }

    public ArtifactStats computePlayerStats(final Research artifact, final PlayerItems playerItems) {
        ArtifactStats stats = new ArtifactStats();
        stats.setArtifact(artifact);

        int level = playerItems.getResearchLevel(artifact.getId());
        stats.setLevel(level);

        Resource mainResource = getMainResource(artifact);

        if (mainResource == null) {
            stats.setReachableLevel(level);
            return stats;
        }

        ResourceAmount amountAvailable = playerItems.getResourceAmount(mainResource);
        stats.setMainResourceAvailable(amountAvailable);

        computeReachableLevel(artifact, level, mainResource, amountAvailable, stats);

        return stats;
    }

    private Resource getMainResource(final Research research) {
        Long resourceId = resourceMap.get(research.getId());
        if (resourceId == null) {
            return null;
        }
        return resourceManager.getResource(resourceId);
    }

    @PostConstruct
    private void computeResourceMaps() {
        for (Research research : researchManager.getResearches()) {
            if (research.getResearchType() != ResearchType.ARTIFACT) {
                continue;
            }

            long researchId = research.getId();
            LOGGER.info("computing main resource for: {}", research.getName());
            Resource mainResource = computeMainResource(research);
            long resourceId = mainResource.getId();
            resourceMap.put(researchId, resourceId);
            reverseResourceMap.put(resourceId, researchId);
        }
    }

    private Resource computeMainResource(final Research research) {
        List<ResearchLevel> levels = research.getLevels();
        if (levels.get(0).getCosts().size() != 1) {
            return null;
        }

        Resource resource = levels.get(0).getCosts().get(0).getResource();

        for (int index = 1; index < levels.size(); index++) {
            ResearchLevel level = levels.get(index);
            if (level.getCosts().size() != 1) {
                return null;
            }
            Resource costResource = level.getCosts().get(0).getResource();
            if (costResource.getStfcSpaceId() != resource.getStfcSpaceId()
                    && costResource.getId() != Resource.ARTIFACT_REMNANT_ID
                    && costResource.getId() != Resource.TEMPORAL_REMNANT_ID) {
                return null;
            }
        }

        return resource;
    }

    private void computeReachableLevel(final Research artifact, final int currentLevel, final Resource mainResource,
            final ResourceAmount amountAvailable, final ArtifactStats stats) {
        long spent = 0;

        int nextLevel = currentLevel + 1;
        StringBuilder log = new StringBuilder();

        log.append(String.format("Total available:  %d\n", amountAvailable.getAmount()));
        log.append(String.format("Current Level:  %d\n", currentLevel));

        while (nextLevel <= artifact.getLevels().size()) {
            List<Cost> costs = artifact.getLevels().get(nextLevel - 1).getCosts();
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