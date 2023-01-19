package club.ryans.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class EventTableAnalyzer implements TableAnalyzer {
    private static final String ROUND = "Round";
    private static final String INDEX = "Battle Event";
    private static final String TYPE = "Type";
    private static final String ATTACKER_NAME = "Attacker Name";
    private static final String ATTACKER_ALLIANCE = "Attacker Alliance";
    private static final String ATTACKER_SHIP = "Attacker Ship";
    private static final String ATTACKER_IS_ARMADA = "Attacker - Is Armada?";
    private static final String TARGET_NAME = "Target Name";
    private static final String TARGET_ALLIANCE = "Target Alliance";
    private static final String TARGET_SHIP = "Target Ship";
    private static final String TARGET_IS_ARMADA = "Target - Is Armada?";
    private static final String CRITICAL_HIT = "Critical Hit?";
    private static final String HULL_DAMAGE = "Hull Damage";
    private static final String SHIELD_DAMAGE = "Shield Damage";
    private static final String MITIGATED_DAMAGE = "Mitigated Damage";
    private static final String TOTAL_DAMAGE = "Total Damage";
    private static final String ABILITY_TYPE = "Ability Type";
    private static final String ABILITY_VALUE = "Ability Value";
    private static final String ABILITY_NAME = "Ability Name";
    private static final String ABILITY_OWNER_NAME = "Ability Owner Name";
    private static final String TARGET_DEFEATED = "Target Defeated";
    private static final String TARGET_DESTROYED = "Target Destroyed";
    private static final String CHARGING_WEAPON_PERCENT = "Charging Weapons %";

    private static final Map<String, Function<String, Object>> CONVERTERS =
            new HashMap<String, Function<String, Object>>(){{
                put(ROUND, Converters::convertInteger);
                put(INDEX, Converters::convertInteger);
                put(TYPE, Converters::convertString);
                put(ATTACKER_NAME, Converters::convertString);
                put(ATTACKER_ALLIANCE, Converters::convertString);
                put(ATTACKER_SHIP, Converters::convertString);
                put(ATTACKER_IS_ARMADA, Converters::convertYesNo);
                put(TARGET_NAME, Converters::convertString);
                put(TARGET_ALLIANCE, Converters::convertString);
                put(TARGET_SHIP, Converters::convertString);
                put(TARGET_IS_ARMADA, Converters::convertYesNo);
                put(CRITICAL_HIT, Converters::convertYesNo);
                put(HULL_DAMAGE, Converters::convertLong);
                put(SHIELD_DAMAGE, Converters::convertLong);
                put(MITIGATED_DAMAGE, Converters::convertLong);
                put(TOTAL_DAMAGE, Converters::convertLong);
                put(ABILITY_TYPE, Converters::convertString);
                put(ABILITY_VALUE, Converters::convertFloatingPoint);
                put(ABILITY_NAME, Converters::convertString);
                put(ABILITY_OWNER_NAME, Converters::convertString);
                put(TARGET_DEFEATED, Converters::convertYesNo);
                put(TARGET_DESTROYED, Converters::convertYesNo);
                put(CHARGING_WEAPON_PERCENT, Converters::convertFloatingPoint);
            }};

    public Map<String, Function<String, Object>> getConverters() {
        return CONVERTERS;
    }

    public void analyze(final Log log, final ParseTable parseTable) {
        List<String> headers = parseTable.getHeaders();

        List<BattleEvent> events = new ArrayList<>();
        for (List<String> parsedRow : parseTable.getRows()) {
            Map<String, Object> convertedRow = convertRow(headers, parsedRow);
            events.add(createEvent(convertedRow));
        }

        log.setEvents(events);
    }

    private BattleEvent createEvent(final Map<String, Object> row) {
        String type = getValue(TYPE, row);
        switch (type) {
            case "Officer Ability":
                return createOfficerProcEvent(row);
            case "Attack":
                return createAttackEvent(row);
            case "Charging Weapons":
                return createChargingWeaponsEvent(row);
            case "Shield Depleted":
                return createShieldDepletedEvent(row);
            case "Combatant Destroyed":
                return createShipDestroyedEvent(row);
        }
        throw new RuntimeException(String.format("Unknown event type: %s", type));
    }

    private OfficerProcEvent createOfficerProcEvent(final Map<String, Object> row) {
        OfficerProcEvent event = new OfficerProcEvent();
        initializeShipEvent(event, row);

        event.setAbilityType(getValue(ABILITY_TYPE, row));
        event.setAbilityName(getValue(ABILITY_NAME, row));
        event.setAbilityOwnerName(getValue(ABILITY_OWNER_NAME, row));
        event.setAbilityValue(getValue(ABILITY_VALUE, row));
        return event;
    }

    private AttackEvent createAttackEvent(final Map<String, Object> row) {
        AttackEvent event = new AttackEvent();
        event.setRound(getValue(ROUND, row));
        event.setIndex(getValue(INDEX, row));

        event.getAttacker().setPlayerName(getValue(ATTACKER_NAME, row));
        event.getAttacker().setAlliance(getValue(ATTACKER_ALLIANCE, row));
        event.getAttacker().setShipName(getValue(ATTACKER_SHIP, row));
        event.setAttackerIsArmada(getValue(ATTACKER_IS_ARMADA, row));

        event.getDefender().setPlayerName(getValue(TARGET_NAME, row));
        event.getDefender().setAlliance(getValue(TARGET_ALLIANCE, row));
        event.getDefender().setShipName(getValue(TARGET_SHIP, row));
        event.setAttackerIsArmada(getValue(TARGET_IS_ARMADA, row));

        boolean critical = getValue(CRITICAL_HIT, row);
        long hull = getValue(HULL_DAMAGE, row);
        long shield = getValue(SHIELD_DAMAGE, row);
        long mitigated = getValue(MITIGATED_DAMAGE, row);
        long total = getValue(TOTAL_DAMAGE, row);
        event.setDamage(new Damage(hull, shield, mitigated, total, critical));
        return event;
    }

    private ChargingWeaponEvent createChargingWeaponsEvent(final Map<String, Object> row) {
        ChargingWeaponEvent event = new ChargingWeaponEvent();
        initializeShipEvent(event, row);

        event.setCharge(getValue(CHARGING_WEAPON_PERCENT, row));
        return event;
    }

    private ShieldDepletedEvent createShieldDepletedEvent(final Map<String, Object> row) {
        ShieldDepletedEvent event = new ShieldDepletedEvent();
        initializeShipEvent(event, row);

        return event;
    }

    private ShipDestroyedEvent createShipDestroyedEvent(final Map<String, Object> row) {
        ShipDestroyedEvent event = new ShipDestroyedEvent();
        initializeShipEvent(event, row);

        return event;
    }

    private void initializeShipEvent(final ShipEvent event, final Map<String, Object> row) {
        event.setRound(getValue(ROUND, row));
        event.setIndex(getValue(INDEX, row));

        event.getShip().setPlayerName(getValue(ATTACKER_NAME, row));
        event.getShip().setAlliance(getValue(ATTACKER_ALLIANCE, row));
        event.getShip().setShipName(getValue(ATTACKER_SHIP, row));
    }
}