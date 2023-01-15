package club.ryans.parser;

import club.ryans.models.ShipClass;
import club.ryans.models.managers.OfficerManager;
import club.ryans.models.managers.ShipManager;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@RequiredArgsConstructor
public class ShipTableAnalyzer implements TableAnalyzer {
    private static final String FLEET_TYPE = "Fleet Type";
    private static final String ATTACK = "Attack";
    private static final String DEFENSE = "Defense";
    private static final String HEALTH = "Health";
    private static final String ABILITY = "Ship Ability";
    private static final String CAPTAIN_MANEUVER = "Captain Maneuver";
    private static final String OFFICER_1_ABILITY = "Officer One Ability";
    private static final String OFFICER_2_ABILITY = "Officer Two Ability";
    private static final String OFFICER_3_ABILITY = "Officer Three Ability";
    private static final String OFFICER_ATTACK_BONUS = "Officer Attack Bonus";
    private static final String DAMAGE_PER_ROUND = "Damage Per Round";
    private static final String ARMOR_PIERCE = "Armour Pierce";
    private static final String SHIELD_PIERCE = "Shield Pierce";
    private static final String ACCURACY = "Accuracy";
    private static final String CRITICAL_CHANCE = "Critical Chance";
    private static final String CRITICAL_DAMAGE = "Critical Damage";
    private static final String OFFICER_DEFENSE_BONUS = "Officer Defense Bonus";
    private static final String ARMOR = "Armour";
    private static final String SHIELD_DEFLECTION = "Shield Deflection";
    private static final String DODGE = "Dodge";
    private static final String OFFICER_HEALTH_BONUS = "Officer Health Bonus";
    private static final String SHIELD_HEALTH = "Shield Health";
    private static final String HULL_HEALTH = "Hull Health";
    private static final String IMPULSE_SPEED = "Impulse Speed";
    private static final String WARP_RANGE = "Warp Range";
    private static final String WARP_SPEED = "Warp Speed";
    private static final String CARGO_CAPACITY = "Cargo Capacity";
    private static final String PROTECTED_CARGO = "Protected Cargo";
    private static final String MINING_BONUS = "Mining Bonus";
    private static final String DEBUFF = "Debuff applied";
    private static final String BUFF = "Buff applied";

    private static final Map<String, Function<String, Object>> CONVERTERS =
            new HashMap<String, Function<String, Object>>(){{
                put(FLEET_TYPE, Converters::convertString);
                put(ATTACK, Converters::convertLong);
                put(DEFENSE, Converters::convertLong);
                put(HEALTH, Converters::convertLong);
                put(ABILITY, Converters::convertString);
                put(CAPTAIN_MANEUVER, Converters::convertString);
                put(OFFICER_1_ABILITY, Converters::convertString);
                put(OFFICER_2_ABILITY, Converters::convertString);
                put(OFFICER_3_ABILITY, Converters::convertString);
                put(OFFICER_ATTACK_BONUS, Converters::convertFloatingPoint);
                put(DAMAGE_PER_ROUND, Converters::convertLong);
                put(ARMOR_PIERCE, Converters::convertLong);
                put(SHIELD_PIERCE, Converters::convertLong);
                put(ACCURACY, Converters::convertLong);
                put(CRITICAL_CHANCE, Converters::convertFloatingPoint);
                put(CRITICAL_DAMAGE, Converters::convertFloatingPoint);
                put(OFFICER_DEFENSE_BONUS, Converters::convertFloatingPoint);
                put(ARMOR, Converters::convertLong);
                put(SHIELD_DEFLECTION, Converters::convertLong);
                put(DODGE, Converters::convertLong);
                put(OFFICER_HEALTH_BONUS, Converters::convertFloatingPoint);
                put(SHIELD_HEALTH, Converters::convertLong);
                put(HULL_HEALTH, Converters::convertLong);
                put(IMPULSE_SPEED, Converters::convertInteger);
                put(WARP_RANGE, Converters::convertInteger);
                put(WARP_SPEED, Converters::convertFloatingPoint);
                put(CARGO_CAPACITY, Converters::convertInteger);
                put(PROTECTED_CARGO, Converters::convertInteger);
                put(MINING_BONUS, Converters::convertFloatingPoint);
                put(DEBUFF, Converters::convertYesNo);
                put(BUFF, Converters::convertYesNo);
            }};

    private final OfficerManager officerManager;
    private final ShipManager shipManager;

    public Map<String, Function<String, Object>> getConverters() {
        return CONVERTERS;
    }

    public void analyze(final Log log, final ParseTable parseTable) {
        List<Ship> ships = new ArrayList<>();
        List<String> headers = parseTable.getHeaders();

        for (List<String> parsedRow : parseTable.getRows()) {
            Map<String, Object> convertedRow = convertRow(headers, parsedRow);
            ships.add(createShip(convertedRow));
        }

        log.setShips(ships);
    }

    private Ship createShip(final Map<String, Object> row) {
        Ship ship = new Ship();
        ship.setFleetType(getValue(FLEET_TYPE, row));
        ship.setAttack(getValue(ATTACK, row));
        ship.setDefense(getValue(DEFENSE, row));
        ship.setHealth(getValue(HEALTH, row));
        ship.setAbility(getValue(ABILITY, row));
        ship.setShipClass(shipManager.getShipClassFromAbility(ship.getAbility()));
        ship.setCaptainManeuver(getValue(CAPTAIN_MANEUVER, row));
        ship.setOfficer1Ability(getValue(OFFICER_1_ABILITY, row));
        ship.setOfficer2Ability(getValue(OFFICER_2_ABILITY, row));
        ship.setOfficer3Ability(getValue(OFFICER_3_ABILITY, row));
        ship.setOfficerAttackBonus(getValue(OFFICER_ATTACK_BONUS, row));
        ship.setDamagePerRound(getValue(DAMAGE_PER_ROUND, row));
        ship.setArmorPierce(getValue(ARMOR_PIERCE, row));
        ship.setShieldPierce(getValue(SHIELD_PIERCE, row));
        ship.setAccuracy(getValue(ACCURACY, row));
        ship.setCriticalChance(getValue(CRITICAL_CHANCE, row));
        ship.setCriticalDamage(getValue(CRITICAL_DAMAGE, row));
        ship.setOfficerDefenseBonus(getValue(OFFICER_DEFENSE_BONUS, row));
        ship.setArmor(getValue(ARMOR, row));
        ship.setShieldDeflection(getValue(SHIELD_DEFLECTION, row));
        ship.setDodge(getValue(DODGE, row));
        ship.setOfficerHealthBonus(getValue(OFFICER_HEALTH_BONUS, row));
        ship.setShieldHealth(getValue(SHIELD_HEALTH, row));
        ship.setHullHealth(getValue(HULL_HEALTH, row));
        ship.setImpulseSpeed(getValue(IMPULSE_SPEED, row));
        ship.setWarpRange(getValue(WARP_RANGE, row));
        ship.setWarpSpeed(getValue(WARP_SPEED, row));
        ship.setCargoCapacity(getValue(CARGO_CAPACITY, row));
        ship.setProtectedCargo(getValue(PROTECTED_CARGO, row));
        ship.setProtectedCargo(getValue(PROTECTED_CARGO, row));
        ship.setMiningBonus(getValue(MINING_BONUS, row));
        ship.setDebuffApplied(getValue(DEBUFF, row));
        ship.setBuffApplied(getValue(BUFF, row));
        return ship;
    }
}