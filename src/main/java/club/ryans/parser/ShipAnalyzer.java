package club.ryans.parser;

import club.ryans.models.managers.ShipManager;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class ShipAnalyzer {
    private final ShipManager shipManager;

    public ShipAnalysisResult analyze(final BattleType battleType, final List<Player> players, final List<Ship> ships,
            final List<BattleEvent> events) {

        if ((battleType == BattleType.HOSTILE || battleType == BattleType.PVP) && players.size() == ships.size()) {
            return analyzePlayers(players, ships, events);
        }

        return analyzeEvents(players, ships, events);
    }

    private ShipAnalysisResult analyzePlayers(final List<Player> players, final List<Ship> ships,
            final List<BattleEvent> events) {
        Map<ShipIdentifier, Ship> shipMap = new HashMap();

        for (int i = 0; i < players.size(); i++) {
            Player player = players.get(i);
            Ship ship = ships.get(i);

            ShipIdentifier shipIdentifier = new ShipIdentifier();
            shipIdentifier.setPlayerName(player.getName());
            shipIdentifier.setShipName(player.getShipName());
            shipIdentifier.setAlliance(getAlliance(player.getName(), player.getShipName(), events));

            ship.setShipIdentifier(shipIdentifier);
            shipMap.put(shipIdentifier, ship);
        }

        return new ShipAnalysisResult(shipMap);
    }

    private ShipAnalysisResult analyzeEvents(final List<Player> players, final List<Ship> ships,
            final List<BattleEvent> events) {
        Map<ShipIdentifier, Ship> shipMap = new HashMap();

        for (BattleEvent event : events) {
            List<ShipIdentifier> identifiers = getIdentifiers(event);
            for (ShipIdentifier identifier : identifiers) {
                if (!shipMap.containsKey(identifier)) {
                    Ship ship = new Ship();
                    ship.setShipIdentifier(identifier);
                    ship.setShipClass(shipManager.getShipClassFromName(identifier.getShipName()));
                    shipMap.put(identifier, ship);
                }
            }
        }

        return new ShipAnalysisResult(shipMap);
    }

    private String getAlliance(final String playerName, final String shipName, final List<BattleEvent> events) {
        for (BattleEvent event : events) {
            List<ShipIdentifier> identifiers = getIdentifiers(event);
            for (ShipIdentifier ship : identifiers) {
                if (ship.getPlayerName().equals(playerName) && ship.getShipName().equals(shipName)) {
                    return ship.getAlliance();
                }
            }
        }
        return null;
    }

    private List<ShipIdentifier> getIdentifiers(final BattleEvent event) {
        if (event instanceof ShipEvent) {
            ShipEvent shipEvent = (ShipEvent) event;
            return Arrays.asList(shipEvent.getShip());
        }

        if (event instanceof AttackEvent) {
            AttackEvent attackEvent = (AttackEvent) event;
            ShipIdentifier attacker = attackEvent.getAttacker();
            ShipIdentifier defender = attackEvent.getDefender();
            return Arrays.asList(attacker, defender);
        }

        return Arrays.asList();
    }
}