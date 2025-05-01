package club.ryans.views.inventory;

import club.ryans.models.items.Resource;
import lombok.Data;

import java.util.List;

@Data
public class InventorySubgroupDescriptor {
    private String name;
    private String description;
    private List<Resource> resources;
}
