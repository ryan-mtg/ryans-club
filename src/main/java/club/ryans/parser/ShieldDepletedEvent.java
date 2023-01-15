package club.ryans.parser;

import lombok.Data;

@Data
public class ShieldDepletedEvent extends ShipEvent {
    private static final String TYPE = "Shield Depleted Event";

    public String getType() {
        return TYPE;
    }
}
