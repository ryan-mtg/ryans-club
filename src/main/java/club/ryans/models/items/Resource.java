package club.ryans.models.items;

import club.ryans.models.assets.AssetType;
import lombok.Data;

@Data
public class Resource extends Item {
    public static final long CRYSTAL_3_STAR_ID = 10;
    public static final long CRYSTAL_4_STAR_ID = 13;
    public static final long CRYSTAL_5_STAR_ID = 268;
    public static final long ORE_3_STAR_ID = 8;
    public static final long ORE_4_STAR_ID = 11;
    public static final long ORE_5_STAR_ID = 266;
    public static final long ISO_1_STAR_ID = 4001;
    public static final long ISO_2_STAR_ID = 4002;
    public static final long ISO_3_STAR_ID = 4003;

    public static final long PARSTEEL_ID = 61;
    public static final long TRITANIUM_ID = 62;
    public static final long DILITHIUM_ID = 63;

    private String shortName;

    private int grade;
    private int rarity;

    public String getStyle() {
        // TODO: change this to a Rarity enum
        switch(rarity) {
            case 2:
                return "rarity-uncommon";
            case 3:
                return "rarity-rare";
            case 4:
                return "rarity-epic";
            default:
                return "rarity-common";
        }
    }

    @Override
    public void inflate(final Inflator inflator) {
        inflateArtPath(inflator, AssetType.RESOURCE, getArtId());
    }
}
