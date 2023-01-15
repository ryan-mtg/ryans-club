package club.ryans.parser;

import lombok.Data;

@Data
public class ShipDestroyedEvent extends ShipEvent {
    private static final String TYPE = "Ship Destroyed Event";

    public String getType() {
        return TYPE;
    }
}
