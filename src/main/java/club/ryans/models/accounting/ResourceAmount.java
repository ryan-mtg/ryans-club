package club.ryans.models.accounting;

import club.ryans.models.items.Resource;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResourceAmount {
    private Resource resource;
    private long amount;

    public long getResourceId() {
        return resource.getId();
    }
}
