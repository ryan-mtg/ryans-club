package club.ryans.models.player;

import club.ryans.models.Mission;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
@AllArgsConstructor
public class MissionStatus {
    private Mission mission;
    private Status status;

    public enum Status {
        UNSTARTED,
        ACTIVE,
        COMPLETE,
    }

    public long getMissionId() {
        return mission.getId();
    }
}
