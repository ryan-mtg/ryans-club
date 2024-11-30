package club.ryans.models.player;

import club.ryans.models.ShipClass;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Ship {
    private long id;
    private ShipClass shipClass;
    private int tier;
    private int level;

    public long getShipClassId() {
        return shipClass.getId();
    }
}