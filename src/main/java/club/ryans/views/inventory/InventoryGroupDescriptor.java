package club.ryans.views.inventory;

import lombok.Data;

import java.util.List;

@Data
public class InventoryGroupDescriptor {
    private String name;
    private String id;
    private boolean main;
    private List<InventorySubgroupDescriptor> subgroups;
}
