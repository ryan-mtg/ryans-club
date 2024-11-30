package club.ryans.models;

public enum Rarity {
    COMMON("common"),
    UNCOMMON("uncommon"),
    RARE("rare"),
    EPIC("epic");

    private String name;

    Rarity(final String name) {
        this.name = name;
    }

    public static Rarity fromInt(final int rarity) {
        switch (rarity) {
            case 1:
                return COMMON;
            case 2:
                return UNCOMMON;
            case 3:
                return RARE;
            case 4:
                return EPIC;
            default:
                throw new IllegalArgumentException("unknown rarity: " + rarity);
        }
    }

    public String toStyle() {
        return String.format("rarity-%s", name);
    }
}
