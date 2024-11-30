package club.ryans.utility.parser;

import club.ryans.models.ShipClass;
import club.ryans.models.managers.ShipManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ShipClassParser {
    private final ShipManager shipManager;

    public ShipClass parseValue(final String text) {
        ShipClass shipClass = shipManager.getShipClassFromName(text);
        if (shipClass == null) {
            Parse.throwError("Unknown shipClass: %s", text);
        }
        return shipClass;
    }
}
