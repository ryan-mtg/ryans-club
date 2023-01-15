package club.ryans.parser;

import lombok.Data;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Data
public class Log {
    private Instant time;
    private String location;
    private BattleType type;
    private boolean outcome;
    private int rounds;
    private int lines;

    private List<Player> players;
    private List<Reward> rewards;
    private List<Ship> ships;
    private List<BattleEvent> events;
    private List<Table> unknownTables = new ArrayList<>();
}