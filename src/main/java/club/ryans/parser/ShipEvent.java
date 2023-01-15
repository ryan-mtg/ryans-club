package club.ryans.parser;

import lombok.Data;

@Data
public abstract class ShipEvent extends BattleEvent {
    private ShipIdentifier ship = new ShipIdentifier();
}
