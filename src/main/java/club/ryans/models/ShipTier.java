package club.ryans.models;

import lombok.Data;

import java.time.Duration;
import java.util.List;

@Data
public class ShipTier {
    private int tier;
    private Duration duration;
    private List<ShipComponent> components;
}
