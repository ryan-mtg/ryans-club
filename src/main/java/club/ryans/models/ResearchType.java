package club.ryans.models;

public enum ResearchType {
    RESEARCH,
    REFIT,
    FAVOR,
    FLEET_COMMANDER_SKILL,
    ARTIFACT;

    public static ResearchType convert(final int type) {
        switch (type) {
            case 0:
                return RESEARCH;
            case 1:
                return REFIT;
            case 2:
                return FAVOR;
            case 3:
                return FLEET_COMMANDER_SKILL;
            case 4:
                return ARTIFACT;
        }
        throw new RuntimeException("Unknown ResearchType: " + type);
    }
}
