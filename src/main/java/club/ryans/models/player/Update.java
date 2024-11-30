package club.ryans.models.player;

import club.ryans.models.accounting.ResourceAmount;
import lombok.Data;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class Update {
    public enum State {
        PENDING,
        COMPLETE,
        ERROR
    }

    private final int id;
    private final User user;
    private final String json;
    private final Instant timestamp;
    private State state;
    private String errorMessage;
    private Map<String, Integer> counts = new HashMap<>();

    private List<ResourceAmount> resources = new ArrayList<>();
    private List<BuildingLevel> buildings = new ArrayList<>();
    private List<Ship> ships = new ArrayList<>();
    private List<OfficerLevel> officers = new ArrayList<>();
    private List<ResearchLevel> researchLevels = new ArrayList<>();
    private List<MissionStatus> missionStatuses = new ArrayList<>();

    public Update(final int id, final User user, final String json) {
        this.id = id;
        this.user = user;
        this.json = json;
        this.timestamp = Instant.now();
        this.state = State.PENDING;
    }

    public String getStyle() {
        switch (state) {
            case ERROR:
                return "state-error";
            case PENDING:
                return "state-pending";
            case COMPLETE:
                return "state-complete";
        }
        return "state-unknown";
    }

    public void setError(final String errorMessage) {
        state = State.ERROR;
        setErrorMessage(errorMessage);
    }

    public void addItemCount(final String type) {
        counts.put(type, 1 + counts.getOrDefault(type, 0));
    }

    public void addResource(final ResourceAmount resourceAmount) {
        resources.add(resourceAmount);
    }

    public void addBuilding(final BuildingLevel building) {
        buildings.add(building);
    }

    public void addShip(final Ship ship) {
        ships.add(ship);
    }

    public void addOfficer(final OfficerLevel officer) {
        officers.add(officer);
    }

    public void addResearch(final ResearchLevel researchLevel) {
        researchLevels.add(researchLevel);
    }

    public void addMission(final MissionStatus missionStatus) {
        missionStatuses.add(missionStatus);
    }
}