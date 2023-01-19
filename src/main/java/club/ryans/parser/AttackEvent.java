package club.ryans.parser;

import lombok.Data;

@Data
public class AttackEvent extends BattleEvent {
    private static final String TYPE = "Attack Event";

    private ShipIdentifier attacker = new ShipIdentifier();
    private boolean attackerIsArmada;

    private ShipIdentifier defender = new ShipIdentifier();
    private boolean defenderIsArmada;

    private boolean criticalHit;

    private Damage damage = new Damage();

    public String getType() {
        return TYPE;
    }
}
