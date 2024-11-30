package club.ryans.views.inventory.annotators;

import club.ryans.models.player.PlayerItems;
import club.ryans.views.inventory.InventoryItem;

import java.util.Collection;
import java.util.List;

public interface InventoryAnnotator {
    List<String> getAnnotations(InventoryItem item, PlayerItems playerItems);
    Collection<Long> getApplicableResourceIds();
}
