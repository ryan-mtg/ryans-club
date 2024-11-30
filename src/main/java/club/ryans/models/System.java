package club.ryans.models;

import club.ryans.utility.Flags;
import lombok.Data;

@Data
public class System {
    private static final int HOUSING_FLAG = 1;
    private static final int MINING_FLAG = 2;
    private static final int MISSIONS_FLAG = 4;
    private static final int HAZARDS_FLAG = 8;
    private static final int DEEP_SPACE_FLAG = 0x10;
    private static final int MIRROR_FLAG = 0x20;
    private static final int WAVE_DEFENSE_FLAG = 0x40;

    private long id;
    private int flags;
    private String name;
    private String narrative;
    private String dialogue;
    private String dilemmaDescription;
    private int level;
    private int x;
    private int y;

    private int warp;
    private int warpWithSuperHighway;

    public void setDilemmaDescription(final String s) {
        dilemmaDescription = s;
    }

    public void setHousing(final boolean hasHousing) {
        flags = Flags.setFlag(flags, HOUSING_FLAG, hasHousing);
    }

    public void setMining(final boolean hasMining) {
        flags = Flags.setFlag(flags, MINING_FLAG, hasMining);
    }

    public void setMissions(final boolean hasMissions) {
        flags = Flags.setFlag(flags, MISSIONS_FLAG, hasMissions);
    }

    public void setHazards(final boolean hasHazards) {
        flags = Flags.setFlag(flags, HAZARDS_FLAG, hasHazards);
    }

    public void setDeepSpace(final boolean isDeepSpace) {
        flags = Flags.setFlag(flags, DEEP_SPACE_FLAG, isDeepSpace);
    }

    public void setMirror(final boolean isMirror) {
        flags = Flags.setFlag(flags, MIRROR_FLAG, isMirror);
    }

    public void setWaveDefense(final boolean isWaveDefense) {
        flags = Flags.setFlag(flags, WAVE_DEFENSE_FLAG, isWaveDefense);
    }
}

