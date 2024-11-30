package club.ryans.models.alliance;

import lombok.Data;

import java.util.List;

@Data
public class Alliance {
    private String name;
    private List<TerritoryOwnership> ownerships;
}
