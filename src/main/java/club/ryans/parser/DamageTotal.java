package club.ryans.parser;

import lombok.Data;

@Data
public class DamageTotal {
    private Damage normal = new Damage();
    private Damage critical = new Damage();
    private Damage total = new Damage();

    public void add(final Damage damage) {
        if (damage.isCritical()) {
            critical.add(damage);
        } else {
            normal.add(damage);
        }
        total.add(damage);
    }
}
