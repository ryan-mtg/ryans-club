package club.ryans.models.managers;

import club.ryans.models.Resource;
import club.ryans.models.alliance.Alliance;
import club.ryans.models.alliance.Service;
import club.ryans.models.alliance.System;
import club.ryans.models.alliance.Territory;
import club.ryans.models.alliance.TerritoryOwnership;
import lombok.RequiredArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class AllianceManager {
    private final ResourceManager resourceManager;
    private static Boolean daylightSavings = null;

    public Alliance createPrototype() {
        Alliance alliance = new Alliance();
        alliance.setName("DEFY");

        List<TerritoryOwnership> ownerships = new ArrayList<>();

        ownerships.add(createAylus());
        ownerships.add(createThosz());
        ownerships.add(createBerTho());
        ownerships.add(createTazolka());
        ownerships.add(createBrellan());

        alliance.setOwnerships(ownerships);
        return alliance;
    }

    private Territory createBimasa() {
        Territory territory = createTerritory("Bimasa", DayOfWeek.MONDAY, LocalTime.of(18, 0),
                "Extra 3* iso until 8pm CT");

        List<Service> services = new ArrayList<>();
        services.add(Service.DEFENSE_ENHANCER);
        services.add(Service.IMPROVED_G3_ISOGEN_REFINERY);
        services.add(Service.ADVANCED_PHANTOM_GENERATOR);
        territory.setServices(services);

        List<System> systems = new ArrayList<>();
        systems.add(createSystem("Bimasa Alpha", Resource.CRYSTAL_3_STAR_ID));
        systems.add(createSystem("Bimasa Beta", Resource.ISO_3_STAR_ID));
        territory.setSystems(systems);

        return territory;
    }

    private Territory createBrijac() {
        Territory territory = createTerritory("Brijac", DayOfWeek.THURSDAY, LocalTime.of(17, 0), null);

        List<Service> services = new ArrayList<>();
        services.add(Service.PROTECTED_CARGO_ENHANCER);
        services.add(Service.IMPROVED_G3_ISOGEN_REFINERY);
        services.add(Service.ADVANCED_SURAX_GENERATOR);
        territory.setServices(services);

        List<System> systems = new ArrayList<>();
        systems.add(createSystem("Brijac Alpha", Resource.ORE_3_STAR_ID));
        systems.add(createSystem("Brijac Beta", Resource.ISO_3_STAR_ID));
        territory.setSystems(systems);

        return territory;
    }

    private Territory createBeku() {
        Territory territory = createTerritory("Beku", DayOfWeek.FRIDAY, LocalTime.of(18, 0),
                "Extra 1* & 2* iso until 8pm CT");

        List<Service> services = new ArrayList<>();
        services.add(Service.SARCOPHAGUS_MATERIAL_FORGES);
        services.add(Service.PVP_COMBAT_ENHANCER);
        services.add(Service.IMPROVED_G1_ISOGEN_REFINERY);
        services.add(Service.IMPROVED_G2_ISOGEN_REFINERY);
        services.add(Service.PHANTOM_PARTICLE_GENERATOR);
        services.add(Service.ENERGY_WEAPONS_ENHANCER);
        territory.setServices(services);

        List<System> systems = new ArrayList<>();
        systems.add(createSystem("Beku Alpha", Resource.ISO_1_STAR_ID));
        systems.add(createSystem("Beku Beta", Resource.ISO_2_STAR_ID));
        territory.setSystems(systems);

        return territory;
    }

    private Territory createBarasa() {
        Territory territory = createTerritory("Barasa", DayOfWeek.SATURDAY, LocalTime.of(19, 0),
                "Advanced Repair Enhancer only on special occasions");

        List<Service> services = new ArrayList<>();
        services.add(Service.METREON_PARTICLE_GENERATOR);
        services.add(Service.ISS_JELLYFISH_CONSTRUCTOR);
        services.add(Service.ADVANCED_REPAIR_ENHANCER);
        services.add(Service.REPAIR_ENHANCER);
        services.add(Service.CHRONOMETRIC_PARTICLE_GENERATOR);
        territory.setServices(services);

        List<System> systems = new ArrayList<>();
        systems.add(createSystem("Barasa Alpha", Resource.ORE_3_STAR_ID));
        systems.add(createSystem("Barasa Beta", Resource.ORE_4_STAR_ID));
        systems.add(createSystem("Barasa Gamma", Resource.ORE_5_STAR_ID));
        territory.setSystems(systems);

        return territory;
    }

    private TerritoryOwnership createBrellan() {
        Territory territory = createTerritory("Brellan", DayOfWeek.SATURDAY, LocalTime.of(21, 0),
            "Advanced Hull Enhancer only on special occasions");

        List<Service> services = new ArrayList<>();
        services.add(Service.METREON_PARTICLE_GENERATOR);
        services.add(Service.ISS_JELLYFISH_CONSTRUCTOR);
        services.add(Service.HULL_ENHANCER);
        services.add(Service.ADVANCED_HULL_ENHANCER);
        services.add(Service.CHRONOMETRIC_PARTICLE_GENERATOR);
        services.add(Service.MONAVEEN_CONSTRUCTION_YARD);
        territory.setServices(services);

        List<System> systems = new ArrayList<>();
        systems.add(createSystem("Brellan Alpha", Resource.CRYSTAL_3_STAR_ID));
        systems.add(createSystem("Brellan Beta", Resource.CRYSTAL_4_STAR_ID));
        systems.add(createSystem("Brellan Gamma", Resource.CRYSTAL_5_STAR_ID));
        territory.setSystems(systems);

        TerritoryOwnership ownership = new TerritoryOwnership();
        ownership.setTerritory(territory);

        Map<String, Boolean> serviceUseMap = new HashMap<>();
        serviceUseMap.put(Service.METREON_PARTICLE_GENERATOR.getName(), true);
        serviceUseMap.put(Service.ISS_JELLYFISH_CONSTRUCTOR.getName(), true);
        serviceUseMap.put(Service.HULL_ENHANCER.getName(), true);
        serviceUseMap.put(Service.ADVANCED_HULL_ENHANCER.getName(), false);
        serviceUseMap.put(Service.CHRONOMETRIC_PARTICLE_GENERATOR.getName(), true);
        serviceUseMap.put(Service.MONAVEEN_CONSTRUCTION_YARD.getName(), true);
        ownership.setServiceUseMap(serviceUseMap);

        return ownership;
    }

    private TerritoryOwnership createTazolka() {
        Territory territory = createTerritory("Tazolka", DayOfWeek.FRIDAY, LocalTime.of(20, 0), null);

        List<Service> services = new ArrayList<>();
        services.add(Service.SARCOPHAGUS_MATERIAL_FORGES);
        services.add(Service.DODGE_ENHANCER);
        services.add(Service.IMPROVED_G1_ISOGEN_REFINERY);
        services.add(Service.IMPROVED_G2_ISOGEN_REFINERY);
        services.add(Service.PHANTOM_PARTICLE_GENERATOR);
        services.add(Service.KINETIC_WEAPONS_ENHANCER);
        territory.setServices(services);

        List<System> systems = new ArrayList<>();
        systems.add(createSystem("Tazolka Alpha", Resource.CRYSTAL_3_STAR_ID));
        systems.add(createSystem("Tazolka Beta", Resource.ISO_2_STAR_ID));
        territory.setSystems(systems);

        TerritoryOwnership ownership = new TerritoryOwnership();
        ownership.setTerritory(territory);

        Map<String, Boolean> serviceUseMap = new HashMap<>();
        serviceUseMap.put(Service.SARCOPHAGUS_MATERIAL_FORGES.getName(), false);
        serviceUseMap.put(Service.DODGE_ENHANCER.getName(), true);
        serviceUseMap.put(Service.IMPROVED_G1_ISOGEN_REFINERY.getName(), false);
        serviceUseMap.put(Service.IMPROVED_G2_ISOGEN_REFINERY.getName(), false);
        serviceUseMap.put(Service.PHANTOM_PARTICLE_GENERATOR.getName(), true);
        serviceUseMap.put(Service.KINETIC_WEAPONS_ENHANCER.getName(), true);
        ownership.setServiceUseMap(serviceUseMap);

        return ownership;
    }

    private TerritoryOwnership createThosz() {
        Territory territory = createTerritory("Thosz", DayOfWeek.WEDNESDAY, LocalTime.of(20, 0), null);

        List<Service> services = new ArrayList<>();
        services.add(Service.SARCOPHAGUS_MATERIAL_FORGES);
        services.add(Service.COMPONENT_EFFICIENCY_ENHANCER);
        services.add(Service.IMPROVED_G1_ISOGEN_REFINERY);
        services.add(Service.IMPROVED_G2_ISOGEN_REFINERY);
        services.add(Service.FKR_REPUTATION_ENHANCER);
        services.add(Service.QUANTUM_PARTICLE_GENERATOR);
        territory.setServices(services);

        List<System> systems = new ArrayList<>();
        systems.add(createSystem("Thosz Alpha", Resource.ORE_3_STAR_ID));
        systems.add(createSystem("Thosz Beta", Resource.ISO_2_STAR_ID));
        territory.setSystems(systems);

        TerritoryOwnership ownership = new TerritoryOwnership();
        ownership.setTerritory(territory);

        Map<String, Boolean> serviceUseMap = new HashMap<>();
        serviceUseMap.put(Service.SARCOPHAGUS_MATERIAL_FORGES.getName(), false);
        serviceUseMap.put(Service.COMPONENT_EFFICIENCY_ENHANCER.getName(), true);
        serviceUseMap.put(Service.IMPROVED_G1_ISOGEN_REFINERY.getName(), false);
        serviceUseMap.put(Service.IMPROVED_G2_ISOGEN_REFINERY.getName(), false);
        serviceUseMap.put(Service.FKR_REPUTATION_ENHANCER.getName(), true);
        serviceUseMap.put(Service.QUANTUM_PARTICLE_GENERATOR.getName(), false);
        ownership.setServiceUseMap(serviceUseMap);

        return ownership;
    }

    private TerritoryOwnership createAylus() {
        Territory territory = createTerritory("Aylus", DayOfWeek.SUNDAY, LocalTime.of(21, 0),
                "Extra 1* & 2* iso until 9:30pm CT");

        List<Service> services = new ArrayList<>();
        services.add(Service.SARCOPHAGUS_MATERIAL_FORGES);
        services.add(Service.CRYSTAL_MINING_ENHANCER);
        services.add(Service.IMPROVED_G1_ISOGEN_REFINERY);
        services.add(Service.IMPROVED_G2_ISOGEN_REFINERY);
        services.add(Service.QUANTUM_PARTICLE_GENERATOR);
        services.add(Service.MERIDIAN_CONSTRUCTION_YARD);
        territory.setServices(services);

        List<System> systems = new ArrayList<>();
        systems.add(createSystem("Aylus Alpha", Resource.ISO_1_STAR_ID));
        systems.add(createSystem("Aylus Beta", Resource.ISO_2_STAR_ID));
        territory.setSystems(systems);

        TerritoryOwnership ownership = new TerritoryOwnership();
        ownership.setTerritory(territory);
        Map<String, Boolean> serviceUseMap = new HashMap<>();
        serviceUseMap.put(Service.SARCOPHAGUS_MATERIAL_FORGES.getName(), false);
        serviceUseMap.put(Service.CRYSTAL_MINING_ENHANCER.getName(), true);
        serviceUseMap.put(Service.IMPROVED_G1_ISOGEN_REFINERY.getName(), false);
        serviceUseMap.put(Service.IMPROVED_G2_ISOGEN_REFINERY.getName(), false);
        serviceUseMap.put(Service.QUANTUM_PARTICLE_GENERATOR.getName(), true);
        serviceUseMap.put(Service.MERIDIAN_CONSTRUCTION_YARD.getName(), true);
        ownership.setServiceUseMap(serviceUseMap);

        return ownership;
    }

    private TerritoryOwnership createDuportas() {
        Territory territory = createTerritory("Duportas", DayOfWeek.MONDAY, LocalTime.of(19, 0),
                null);

        List<Service> services = new ArrayList<>();
        services.add(Service.PIERCING_ENHANCER);
        services.add(Service.IMPROVED_G3_ISOGEN_REFINERY);
        services.add(Service.ADVANCED_PHANTOM_GENERATOR);
        services.add(Service.MONAVEEN_CONSTRUCTION_YARD);
        territory.setServices(services);

        List<System> systems = new ArrayList<>();
        systems.add(createSystem("Duportas Alpha", Resource.CRYSTAL_3_STAR_ID));
        systems.add(createSystem("Duportas Beta", Resource.ISO_3_STAR_ID));
        territory.setSystems(systems);

        TerritoryOwnership ownership = new TerritoryOwnership();
        ownership.setTerritory(territory);
        Map<String, Boolean> serviceUseMap = new HashMap<>();
        serviceUseMap.put(Service.PIERCING_ENHANCER.getName(), true);
        serviceUseMap.put(Service.IMPROVED_G3_ISOGEN_REFINERY.getName(), true);
        serviceUseMap.put(Service.ADVANCED_PHANTOM_GENERATOR.getName(), true);
        serviceUseMap.put(Service.MONAVEEN_CONSTRUCTION_YARD.getName(), false);
        ownership.setServiceUseMap(serviceUseMap);

        return ownership;
    }

    private TerritoryOwnership createBerTho() {
        Territory territory = createTerritory("Ber'Tho", DayOfWeek.THURSDAY, LocalTime.of(21, 0),
            "Extra 3* iso until 9:30pm CT");

        List<Service> services = new ArrayList<>();
        services.add(Service.DEFENSE_PLATFORM_ENHANCER);
        services.add(Service.IMPROVED_G3_ISOGEN_REFINERY);
        services.add(Service.ADVANCED_QUANTUM_GENERATOR);
        services.add(Service.PVP_ISOLYTIC_DAMAGE_ENHANCER);
        territory.setServices(services);

        List<System> systems = new ArrayList<>();
        systems.add(createSystem("Ber'Tho Alpha", Resource.CRYSTAL_4_STAR_ID));
        systems.add(createSystem("Ber'Tho Beta", Resource.ISO_3_STAR_ID));
        territory.setSystems(systems);

        TerritoryOwnership ownership = new TerritoryOwnership();
        ownership.setTerritory(territory);

        Map<String, Boolean> serviceUseMap = new HashMap<>();
        serviceUseMap.put(Service.DEFENSE_PLATFORM_ENHANCER.getName(), true);
        serviceUseMap.put(Service.IMPROVED_G3_ISOGEN_REFINERY.getName(), true);
        serviceUseMap.put(Service.ADVANCED_QUANTUM_GENERATOR.getName(), true);
        serviceUseMap.put(Service.PVP_ISOLYTIC_DAMAGE_ENHANCER.getName(), true);
        ownership.setServiceUseMap(serviceUseMap);

        return ownership;
    }

    private Territory createTerritory(final String name, final DayOfWeek takeoverDay, final LocalTime takeoverTime,
            final String notes) {
        Territory territory = new Territory();
        territory.setName(name);
        territory.setTakeoverDay(takeoverDay);

        if (isDaylightSavings()) {
            territory.setTakeoverTime(takeoverTime.plusHours(-1));
        } else {
            territory.setTakeoverTime(takeoverTime);
        }

        territory.setNotes(notes);

        return territory;
    }

    private System createSystem(final String name, final long... resourceIds) {
        System system = new System();
        system.setName(name);
        system.setResources(Arrays.stream(resourceIds).mapToObj(id -> resourceManager.getResource(id))
                .collect(Collectors.toList()));
        return system;
    }

    private boolean isDaylightSavings() {
        if (daylightSavings == null) {
            Date today = new Date();
            daylightSavings = TimeZone.getTimeZone(ZoneId.of("America/New_York")).inDaylightTime(today);
        }

        return daylightSavings;
    }
}
