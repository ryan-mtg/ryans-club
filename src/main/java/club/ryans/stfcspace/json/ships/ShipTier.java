package club.ryans.stfcspace.json.ships;

import lombok.Data;

import java.util.List;

@Data
public class ShipTier {
    private int tier;
    private long duration;
    private List<Component> components;

    //buffs?
}
