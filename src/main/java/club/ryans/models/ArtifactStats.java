package club.ryans.models;

import club.ryans.models.accounting.ResourceAmount;
import club.ryans.models.items.Research;
import lombok.Data;

@Data
public class ArtifactStats {
    private Research artifact;
    private int level;
    private ResourceAmount mainResourceAvailable;

    private int reachableLevel;
    private ResourceAmount mainResourceNeeded;

    private String log;
}