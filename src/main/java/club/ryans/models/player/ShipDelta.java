package club.ryans.models.player;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
public class ShipDelta {
    private Ship previous;
    private Ship current;

    public long getShipId() {
        return current.getId();
    }
}
