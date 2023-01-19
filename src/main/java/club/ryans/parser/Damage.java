package club.ryans.parser;

import lombok.Data;

@Data
public class Damage {
    private long hull;
    private long shield;
    private long mitigated;
    private long total;
    private boolean critical;

    private int shots;
    private double mitigation;
    private double piercing;

    public Damage(){}

    public Damage(final long hull, final long shield, final long mitigated, final long total, final boolean critical) {
        this.hull = hull;
        this.shield = shield;
        this.mitigated = mitigated;
        this.total = total;
        this.shots = 1;

        this.mitigation = ((double) mitigated) / total;
        this.piercing = ((double) total - mitigated) / total;
    }

    public void add(final Damage damage) {
        this.mitigation = average(this.shots, this.mitigation, damage.getShots(), damage.getMitigation());
        this.piercing = average(this.shots, this.piercing, damage.getShots(), damage.getPiercing());

        this.hull += damage.getHull();
        this.shield += damage.getShield();
        this.mitigated += damage.getMitigated();
        this.total += damage.getTotal();
        this.shots += damage.shots;
    }

    private static double average(final int count1, final double value1, final int count2, final double value2) {
        return (count1 * value1 + count2 * value2) / (count1 + count2);
    }
}
