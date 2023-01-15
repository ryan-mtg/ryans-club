package club.ryans.parser;

import club.ryans.models.ShipClass;
import lombok.Data;

@Data
public class Ship {
    private ShipIdentifier shipIdentifier = new ShipIdentifier();
    private String fleetType;
    private ShipClass shipClass;
    private long attack;
    private long defense;
    private long health;
    private String ability;
    private String captainManeuver;
    private String officer1Ability;
    private String officer2Ability;
    private String officer3Ability;
    private Double officerAttackBonus;
    private long damagePerRound;
    private long armorPierce;
    private long shieldPierce;
    private long accuracy;
    private Double criticalChance;
    private Double criticalDamage;
    private Double officerDefenseBonus;
    private long armor;
    private long shieldDeflection;
    private long dodge;
    private Double officerHealthBonus;
    private long shieldHealth;
    private long hullHealth;
    private int impulseSpeed;
    private int warpRange;
    private double warpSpeed;
    private int cargoCapacity;
    private int protectedCargo;
    private double miningBonus;
    private boolean debuffApplied;
    private boolean buffApplied;

    private boolean survived;
    private int roundsSurvived;
}
