package club.ryans.parser;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;

@Data
public class Player {
    private String name;
    private Integer level;

    @Getter(AccessLevel.PRIVATE)
    private boolean outcome;

    private String shipName;
    private int shipLevel;
    private long shipStrength;
    private int shipXp;
    private String officer1;
    private String officer2;
    private String officer3;
    private long totalHull;
    private long remainingHull;
    private long totalShield;
    private long remainingShield;

    public boolean getOutcome() {
        return outcome;
    }
}