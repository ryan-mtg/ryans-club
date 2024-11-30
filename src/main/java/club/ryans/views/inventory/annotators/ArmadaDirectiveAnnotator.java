package club.ryans.views.inventory.annotators;

import club.ryans.models.player.PlayerItems;
import club.ryans.views.inventory.InventoryItem;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class ArmadaDirectiveAnnotator implements InventoryAnnotator{
    private static final Map<Long, Integer> DIRECTIVES_PER_ARMADA = new HashMap<>();

    static {
        //Regular Armadas
        DIRECTIVES_PER_ARMADA.put(6200L, 100);
        DIRECTIVES_PER_ARMADA.put(6206L, 200);
        DIRECTIVES_PER_ARMADA.put(6207L, 500);

        //Eclipse Armadas
        DIRECTIVES_PER_ARMADA.put(9003L, 100);
        DIRECTIVES_PER_ARMADA.put(9004L, 200);
        DIRECTIVES_PER_ARMADA.put(9005L, 500);

        //Dominion Armadas
        DIRECTIVES_PER_ARMADA.put(47010L, 100);
        DIRECTIVES_PER_ARMADA.put(47011L, 200);
        DIRECTIVES_PER_ARMADA.put(47012L, 500);

        //Borg Armadas
        DIRECTIVES_PER_ARMADA.put(9106L, 1);
        DIRECTIVES_PER_ARMADA.put(7193L, 10);
        DIRECTIVES_PER_ARMADA.put(51015L, 500);

        //Borg Solos
        DIRECTIVES_PER_ARMADA.put(50004L, 100);
        DIRECTIVES_PER_ARMADA.put(50005L, 200);
        DIRECTIVES_PER_ARMADA.put(50006L, 500);

        //Formation Armadas
        DIRECTIVES_PER_ARMADA.put(54201L, 100);
        DIRECTIVES_PER_ARMADA.put(57201L, 200);

        //Xindi Armadas
        DIRECTIVES_PER_ARMADA.put(64100L, 100);
        DIRECTIVES_PER_ARMADA.put(64101L, 500);

        //Doomsday Armadas
        DIRECTIVES_PER_ARMADA.put(9099L, 1);

        //Shipyard Armadas
        DIRECTIVES_PER_ARMADA.put(58005L, 100);
    }

    @Override
    public Collection<Long> getApplicableResourceIds() {
        return DIRECTIVES_PER_ARMADA.keySet();
    }

    @Override
    public List<String> getAnnotations(final InventoryItem item, final PlayerItems playerItems) {
        List<String> annotations = new ArrayList<>();
        long resourceId = item.getResourceId();
        if (!DIRECTIVES_PER_ARMADA.containsKey(resourceId)) {
            return annotations;
        }
        int directiveCost = DIRECTIVES_PER_ARMADA.get(resourceId);
        long directives = item.getAmount();

        if (directives >= directiveCost) {
            annotations.add(String.format("%d Starts", directives / directiveCost));
        } else {
            annotations.add(String.format("%d Needed", directiveCost - directives));
        }

        return annotations;
    }
}
