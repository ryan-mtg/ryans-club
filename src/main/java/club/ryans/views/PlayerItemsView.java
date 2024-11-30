package club.ryans.views;

import club.ryans.models.player.BuildingLevel;
import club.ryans.models.player.OfficerLevel;
import club.ryans.models.player.PlayerItems;
import club.ryans.models.player.ResearchLevel;
import club.ryans.models.accounting.ResourceAmount;
import club.ryans.models.player.Ship;
import club.ryans.views.inventory.Inventory;
import club.ryans.views.inventory.InventoryGrouper;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class PlayerItemsView {
    private List<ResourceAmount> resources = new ArrayList<>();
    private List<BuildingLevel> buildings = new ArrayList<>();
    private List<Ship> ships = new ArrayList<>();
    private List<OfficerLevel> officers = new ArrayList<>();
    private List<ResearchLevel> researchLevels = new ArrayList<>();
    private Inventory inventory;

    public PlayerItemsView(final PlayerItems playerItems, final InventoryGrouper grouper) {
        this.resources.addAll(playerItems.getResources().values());
        this.buildings.addAll(playerItems.getBuildings().values());
        this.ships.addAll(playerItems.getShips().values());
        this.officers.addAll(playerItems.getOfficers().values());
        this.researchLevels.addAll(playerItems.getResearchLevels().values());
        this.inventory = grouper.group(playerItems);

        sort();
    }

    private void sort() {
        this.ships.sort((a, b) -> {
            int at = a.getTier(), bt = b.getTier();
            if (at != bt) {
                return bt - at;
            }
            int al = a.getLevel(), bl = b.getLevel();
            if (al != bl) {
                return bl - al;
            }

            return a.getShipClass().getName().compareToIgnoreCase(b.getShipClass().getName());
        });

        this.officers.sort((a, b) -> {
            int ar = a.getRank(), br = b.getRank();
            if (ar != br) {
                return br - ar;
            }
            int al = a.getLevel(), bl = b.getLevel();
            if (al != bl) {
                return bl - al;
            }

            return a.getOfficer().getName().compareToIgnoreCase(b.getOfficer().getName());
        });
    }
}