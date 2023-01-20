package club.ryans.parser;

import lombok.Data;

@Data
public class DamageTotal {
    private Damage regular = new Damage();
    private Damage critical = new Damage();
    private Damage total = new Damage();

    public void add(final Damage damage) {
        if (damage.isCritical()) {
            critical.add(damage);
        } else {
            regular.add(damage);
        }
        total.add(damage);
    }
}
