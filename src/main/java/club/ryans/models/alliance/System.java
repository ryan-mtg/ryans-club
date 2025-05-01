package club.ryans.models.alliance;

import club.ryans.models.items.Resource;
import lombok.Data;

import java.util.List;

@Data
public class System {
    private String name;
    private List<Resource> resources;
}
