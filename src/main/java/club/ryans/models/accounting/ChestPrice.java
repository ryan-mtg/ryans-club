package club.ryans.models.accounting;

import club.ryans.models.items.Resource;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;
import java.util.List;

@Getter @Setter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ChestPrice {
    private int chests;
    private List<ResourceAmount> prices;

    public static ChestPrice noChests() {
        return new ChestPrice(0, Arrays.asList());
    }

    public static ChestPrice freeChest() {
        return new ChestPrice(1, Arrays.asList());
    }

    public static ChestPrice create(final int chests, final Resource resource, final long amount) {
        return new ChestPrice(chests, Arrays.asList(new ResourceAmount(resource, amount)));
    }

    public static ChestPrice create(final int chests, final List<ResourceAmount> prices) {
        return new ChestPrice(chests, prices);
    }

    public String getName() {
        switch (chests) {
            case 0:
                return "0";
            case 1:
                if (prices.isEmpty()) {
                    return "Free";
                }
            default:
                return Integer.toString(chests);
        }
    }
}