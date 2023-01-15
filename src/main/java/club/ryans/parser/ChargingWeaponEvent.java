package club.ryans.parser;

import lombok.Data;

@Data
public class ChargingWeaponEvent extends ShipEvent {
    private static final String TYPE = "Charging Weapon Event";

    private double charge;

    public String getType() {
        return TYPE;
    }
}
