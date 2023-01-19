package club.ryans.parser;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class DamageReport {
    private DamageTotal dealt = new DamageTotal();
    private DamageTotal received = new DamageTotal();

    private List<RoundDamageStats> rounds = new ArrayList<>();

    public void addDamageDealt(final int round, final Damage damage) {
        dealt.add(damage);

        resizeRounds(round);
        rounds.get(round).addDamageDealt(damage);
    }

    public void addDamageReceived(final int round, final Damage damage) {
        received.add(damage);

        resizeRounds(round);
        rounds.get(round).addDamageReceived(damage);
    }

    private void resizeRounds(final int round) {
        while (rounds.size() <= round) {
            rounds.add(new RoundDamageStats(round));
        }
    }
}