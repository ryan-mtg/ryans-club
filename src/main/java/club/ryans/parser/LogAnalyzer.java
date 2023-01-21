package club.ryans.parser;

import club.ryans.models.managers.OfficerManager;
import club.ryans.models.managers.ShipManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class LogAnalyzer {
    private final OfficerManager officerManager;
    private final ShipManager shipManager;

    public Log analyze(final ParseResult parseResult, final String fileName, final String tag) {
        Log log = new Log();

        for (ParseTable parseTable : parseResult.getTables()) {
            analyzeTable(log, parseTable);
        }

        log.setFileName(fileName);
        log.setTag(tag);
        log.setOutcome(computeOutcome(log));
        log.setType(computeBattleType(log));
        log.setRounds(computeRounds(log.getEvents()));
        log.setLines(parseResult.getLines());

        ShipAnalyzer shipAnalyzer = new ShipAnalyzer(shipManager);
        ShipAnalysisResult result =
                shipAnalyzer.analyze(log.getType(), log.getPlayers(), log.getShips(), log.getEvents());

        Map<ShipIdentifier, Ship> shipMap = result.getShipMap();
        log.setShips(shipMap.values().stream().collect(Collectors.toList()));

        computeRoundsSurvived(log.getRounds(), shipMap, log.getEvents());
        computeDamageStats(shipMap, log.getEvents());

        return log;
    }

    private void analyzeTable(final Log log, final ParseTable parseTable) {
        List<String> headers = parseTable.getHeaders();
        TableAnalyzer tableAnalyzer = createTableAnalyzer(headers.get(0));
        tableAnalyzer.analyze(log, parseTable);
    }

    private TableAnalyzer createTableAnalyzer(final String firstHeader) {
        switch (firstHeader) {
            case "Player Name":
                return new PlayerTableAnalyzer();
            case "Reward Name":
                return new RewardTableAnalyzer();
            case "Fleet Type":
                return new ShipTableAnalyzer(officerManager, shipManager);
            case "Round":
                return new EventTableAnalyzer();
        }
        return new DefaultTableAnalyzer();
    }

    private int computeRounds(final List<BattleEvent> events) {
        if (events == null) {
            return 0;
        }

        int rounds = 0;
        for (BattleEvent event : events) {
            rounds = Math.max(rounds, event.getRound());
        }
        return rounds;
    }

    private BattleType computeBattleType(final Log log) {
        if (log.getPlayers().size() == 1) {
            // assault, or armada,
            Player player = log.getPlayers().get(0);
            if (player.getShipName().contains("STARBASE")) {
                return BattleType.STARBASE_ASSAULT;
            }

            if (player.getShipName().contains("Jem'Hadar")) {
                return BattleType.SOLO_ARMADA;
            }

            if (player.getShipName().contains("Borg Sphere")) {
                return BattleType.SOLO_ARMADA;
            }

            return BattleType.ARMADA;
        }

        if (log.getPlayers().size() == 2) {
            Player opponent = log.getPlayers().get(1);
            if (opponent.getName().equals(opponent.getOfficer1())) {
                return BattleType.HOSTILE;
            }
            return BattleType.PVP;
        }

        return BattleType.HOSTILE;
    }

    private boolean computeOutcome(final Log log) {
        if (log.getPlayers().isEmpty()) {
            return false;
        }

        return !log.getPlayers().get(log.getPlayers().size() - 1).getOutcome();
    }

    private void computeRoundsSurvived(final int rounds, final Map<ShipIdentifier, Ship> shipMap,
            final List<BattleEvent> events) {
        for (Ship ship : shipMap.values()) {
            ship.setSurvived(true);
            ship.setRoundsSurvived(rounds);
            ship.setShieldDropped(false);
        }

        for (BattleEvent event : events) {
            if (event instanceof ShipDestroyedEvent) {
                ShipDestroyedEvent shipDestroyedEvent = (ShipDestroyedEvent) event;

                Ship ship = shipMap.get(shipDestroyedEvent.getShip());
                ship.setSurvived(false);
                ship.setRoundsSurvived(shipDestroyedEvent.getRound());
            }

            if (event instanceof ShieldDepletedEvent) {
                ShieldDepletedEvent shieldDepletedEvent = (ShieldDepletedEvent) event;

                Ship ship = shipMap.get(shieldDepletedEvent.getShip());
                ship.setShieldDropped(true);
                ship.setRoundShieldDropped(shieldDepletedEvent.getRound());
            }
        }
    }

    private void computeDamageStats(final Map<ShipIdentifier, Ship> shipMap, final List<BattleEvent> events) {
        for (BattleEvent event : events) {
            if (event instanceof AttackEvent) {
                AttackEvent attackEvent = (AttackEvent) event;

                int round = attackEvent.getRound();
                Damage damage = attackEvent.getDamage();
                Ship attacker = shipMap.get(attackEvent.getAttacker());
                Ship defender = shipMap.get(attackEvent.getDefender());

                attacker.getDamageReport().addDamageDealt(round, damage);
                defender.getDamageReport().addDamageReceived(round, damage);
            }
        }
    }
}