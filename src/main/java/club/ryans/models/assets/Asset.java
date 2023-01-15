package club.ryans.models.assets;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Asset {
    private int id;
    private AssetType type;
    private String path;
}
