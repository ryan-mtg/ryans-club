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

    private long hullDamage;
    private long shieldDamage;
    private long mitigatedDamage;
    private long totalDamage;

    public String getType() {
        return TYPE;
    }
}
