package club.ryans.views.inventory;

import lombok.Data;

import java.util.List;

@Data
public class Inventory {
    private List<InventoryGroup> groups;
    private String defaultGroupId;
}
