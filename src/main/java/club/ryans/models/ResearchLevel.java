package club.ryans.models;

import lombok.Data;

import java.time.Duration;
import java.util.List;

@Data
public class ResearchLevel {
    private long id;

    private int strength;
    private int strengthIncrease;

    private Duration buildTime;

    private long latinumCost;

    private List<Cost> costs;
    private List<Requirement> requirements;
    private List<Reward> rewards;
}