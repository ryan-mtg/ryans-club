package club.ryans.parser;

import lombok.Data;

@Data
public class RoundDamageStats {
    private int round;
    private DamageTotal dealt = new DamageTotal();
    private DamageTotal received = new DamageTotal();

    public RoundDamageStats(final int round) {
        this.round = round;
    }

    public void addDamageDealt(final Damage damage) {
        dealt.add(damage);
    }

    public void addDamageReceived(final Damage damage) {
        received.add(damage);
    }
}
