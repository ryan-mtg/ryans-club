package club.ryans.models.player;

import club.ryans.models.Officer;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class OfficerLevel {
    private Officer officer;
    private int rank;
    private int level;

    public long getOfficerId() {
        return officer.getId();
    }
}
