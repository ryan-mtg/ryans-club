package club.ryans.models;

import club.ryans.models.items.Resource;
import lombok.Data;

@Data
public class Cost {
    private long resourceId;

    private long amount;

    private Resource resource;
}
