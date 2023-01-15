package club.ryans.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class PlayerTableAnalyzer implements TableAnalyzer {
    private static final String NAME = "Player Name";
    private static final String LEVEL = "Player Level";
    private static final String OUTCOME = "Outcome";
    private static final String SHIP_NAME = "Ship Name";
    private static final String SHIP_LEVEL = "Ship Level";
    private static final String SHIP_STRENGTH = "Ship Strength";
    private static final String SHIP_XP = "Ship XP";
    private static final String OFFICER_1 = "Officer One";
    private static final String OFFICER_2 = "Officer Two";
    private static final String OFFICER_3 = "Officer Three";
    private static final String TOTAL_HULL = "Hull Health";
    private static final String REMAINING_HULL = "Hull Health Remaining";
    private static final String TOTAL_SHIELD = "Shield Health";
    private static final String REMAINING_SHIELD = "Shield Health Remaining";
    private static final String LOCATION = "Location";
    private static final String TIME = "Timestamp";

    private static final Map<String, Function<String, Object>> CONVERTERS =
            new HashMap<String, Function<String, Object>>(){{
                put(NAME, Converters::convertString);
                put(LEVEL, Converters::convertInteger);
                put(OUTCOME, Converters::convertString);
                put(SHIP_NAME, Converters::convertString);
                put(SHIP_LEVEL, Converters::convertInteger);
                put(SHIP_STRENGTH, Converters::convertLong);
                put(SHIP_XP, Converters::convertInteger);
                put(OFFICER_1, Converters::convertString);
                put(OFFICER_2, Converters::convertString);
                put(OFFICER_3, Converters::convertString);
                put(TOTAL_HULL, Converters::convertLong);
                put(REMAINING_HULL, Converters::convertLong);
                put(TOTAL_SHIELD, Converters::convertLong);
                put(REMAINING_SHIELD, Converters::convertLong);
                put("Location", Converters::convertString);
                put("Timestamp", Converters::convertTime);
            }};

    public Map<String, Function<String, Object>> getConverters() {
        return CONVERTERS;
    }

    public void analyze(final Log log, final ParseTable parseTable) {
        List<Player> players = new ArrayList<>();
        List<String> headers = parseTable.getHeaders();

        for (List<String> parsedRow : parseTable.getRows()) {
            Map<String, Object> convertedRow = convertRow(headers, parsedRow);
            players.add(createPlayer(convertedRow));
            log.setLocation(getValue(LOCATION, convertedRow));
            log.setTime(getValue(TIME, convertedRow));
        }

        log.setPlayers(players);
    }

    private Player createPlayer(final Map<String, Object> row) {
        Player player = new Player();
        player.setName(getValue(NAME, row));
        player.setLevel(getValue(LEVEL, row));
        player.setOutcome("VICTORY".equals(getValue(OUTCOME, row)));
        player.setShipName(getValue(SHIP_NAME, row));
        player.setShipLevel(getValue(SHIP_LEVEL, row));
        player.setShipStrength(getValue(SHIP_STRENGTH, row));
        player.setShipXp(getValue(SHIP_XP, row));
        player.setOfficer1(getValue(OFFICER_1, row));
        player.setOfficer2(getValue(OFFICER_2, row));
        player.setOfficer3(getValue(OFFICER_3, row));
        player.setTotalHull(getValue(TOTAL_HULL, row));
        player.setRemainingHull(getValue(REMAINING_HULL, row));
        player.setTotalShield(getValue(TOTAL_SHIELD, row));
        player.setRemainingShield(getValue(REMAINING_SHIELD, row));
        return player;
    }
}