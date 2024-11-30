package club.ryans.models.alliance;

import lombok.Getter;

@Getter
public enum Service {
    ACCURACY_ENHANCER("Accuracy Enhancer", "+40% base Accuracy for all ships"),
    ADVANCED_DAMAGE_ENHANCER("Advanced Damage Enhancer", "Up to +700% base Weapon Damage to all ships"),
    ADVANCED_HULL_ENHANCER("Advanced Hull Enhancer", "Up to +500% base Hull Health to all ships"),
    ADVANCED_PHANTOM_GENERATOR("Advanced Phantom Generator", "Grants Phantom Particles each day in the Territory Store (Ops 25+)"),
    ADVANCED_QUANTUM_GENERATOR("Advanced Quantum Generator", "Grants Quantum Particles each day in the Territory Store (Ops 25+)"),
    ADVANCED_REPAIR_ENHANCER("Advanced Repair Enhancer", "Up to +400% base Repair Cost Efficiency for repairing ships"),
    ADVANCED_SURAX_GENERATOR("Advanced Surax Generator", "Grants Surax Particles each day in the Territory Store (Ops 25+)"),

    CHRONOMETRIC_PARTICLE_GENERATOR("Chronometric Particle Generator", "Grants Chronometric Particles each day in the Territory Store (Ops 25+)"),
    COMPONENT_EFFICIENCY_ENHANCER("Component Efficiency Enhancer", "+30% base Ship Component Efficiency for Parsteel, Tritanium and Dilithium costs (including Sigma)"),
    CRYSTAL_MINING_ENHANCER("Crystal Mining Enhancer", "+40% base Crystal Mining Rate for all ships	Gelida"),

    DAMAGE_ENHANCER("Damage Enhancer", "+50% base Weapon Damage to all ships"),
    DEFENSE_ENHANCER("Defense Enhancer", "+80% base Shield Deflection, Armor and Dodge to all ships"),
    DEFENSE_PLATFORM_ENHANCER("Defense Platform Enhancer", "+70% base Weapon Damage to all Station Defense Platforms"),
    DODGE_ENHANCER("Dodge Enhancer", "+40% base Dodge for all ships"),

    ENERGY_WEAPONS_ENHANCER("Energy Weapons Enhancer", "Increases Energy Weapons damage up to 150% for all ships"),
    FKR_REPUTATION_ENHANCER("FKR Reputation Enhancer", "Increases positive Reputation for FKR up to 40% when defeating hostiles"),

    HULL_ENHANCER("Hull Enhancer", "+50% base Hull Health to all ships"),

    IMPROVED_G1_ISOGEN_REFINERY("Improved 1* Isogen Refinery", "Grants improved 1* Isogen Refines (Ops 25+)", "improved_g1_isogen_refinery.png"),
    IMPROVED_G2_ISOGEN_REFINERY("Improved 2* Isogen Refinery", "Grants improved 2* Isogen Refines (Ops 25+)", "improved_g2_isogen_refinery.png"),
    IMPROVED_G3_ISOGEN_REFINERY("Improved 3* Isogen Refinery", "Grants improved 3* Isogen Refines (Ops 25+)", "improved_g3_isogen_refinery.png"),
    ISS_JELLYFISH_CONSTRUCTOR("ISS Jellyfish Constructor", "Lets members purchase ISS Jellyfish Blueprints for Iso-Emulsion each week in the Territory Store (Ops 39+)"),

    KINETIC_WEAPONS_ENHANCER("Kinetic Weapons Enhancer", "Increases Kinetic Weapons damage up to 150% for all ships"),

    MERIDIAN_CONSTRUCTION_YARD("Meridian Construction Yard", "Lets Alliance members purchase Meridian Blueprints for Iso-Emulsion in the Territory store, once per week. (Level 25+)"),
    METREON_PARTICLE_GENERATOR("Metreon Particle Generator", "Grants Metreon Particles each day in the Territory Store (Ops 25+)"),
    MONAVEEN_CONSTRUCTION_YARD("Monaveen Construction Yard", "Lets members purchase Monaveen Blueprints for Iso-Emulsion each week in the Territory Store (Ops 39+)"),

    PHANTOM_PARTICLE_GENERATOR("Phantom Particle Generator", "Grants Phantom Particles each day in the Territory Store (Ops 25+)"),
    PIERCING_ENHANCER("Piercing Enhancer", "+80% base Accuracy, Shield Piercing and Armor Piercing to all ships"),
    PROTECTED_CARGO_ENHANCER("Protected Cargo Enhancer", "+50% base Protected Cargo for all ships"),
    PVP_COMBAT_ENHANCER("PvP Combat Enhancer", "+25% base Weapon Damage to all ships when in PvP"),
    PVP_ISOLYTIC_DAMAGE_ENHANCER("PvP Isolytic Damage Enhancer", "Increases Isolytic Damage up to 5% to all ships when in PvP"),

    QUANTUM_PARTICLE_GENERATOR("Quantum Particle Generator", "Grants Quantum Particles each day in the Territory Store (Ops 25+)"),

    REPAIR_ENHANCER("Repair Enhancer", "+75% base Repair Cost Efficiency for repairing ships"),

    SARCOPHAGUS_MATERIAL_FORGES("Sarcophagus Material Forges", "Grants the ability to forge the materials necessary to upgrade the Sarcophagus each day (Ops 30+)"),

    /*
    Advanced Mining Enhancer	Up to +300% base Mining Rate for all ships	Corva
    Advanced Officer Enhancer	Up to +250% base Attack, Defense and Health to all officers	Tholus
    Advanced Shield Enhancer	Up to +500% base Shield Health to all ships	Barasa
    Armada Combat Enhancer	+25% base Weapon Damage to all ships when attacking Armada Targets	Klefaski, Thaylen
    Armor Enhancer	+40% base Armor for all ships	Qeyma, Tigan
    Armor Piercing Enhancer	+40% base Armor Piercing for all ships	Zamaro, Temeri
    Cloaking Duration Enhancer	Increases Cloaking Duration up to 15% for all ships	Nujord, Lenara
    Construction Efficiency Enhancer	+20% base Building Construction Efficiency for Parsteel, Tritanium and Dilithium costs (including Sigma)	Perim, Stilhe
    Construction Speed Enhancer	+50% base Construction Speed for all buildings	Zhian, Bolari
    Deflection Enhancer	+40% base Shield Deflection for all ships	Helvi, Ruhe
    Gas Mining Enhancer	+40% base Gas Mining Rate for all ships	Innlasn, Aonad
    Hostile Combat Enhancer	+25% base Weapon Damage to all ships when fighting hostiles	Kolava, Tola, Ezla
    Mining Enhancer	+50% base Mining Rate for all ships	Corva
    Officer Attack Enhancer	+40% base Attack to all Officers	Vantar, Avansa
    Officer Defense Enhancer	+40% base Defense to all Officers	Burran, Hoobishan
    Officer Enhancer	+35% base Attack, Defense and Health to all officers	Tholus
    Ore Mining Enhancer	+40% base Ore Mining Rate for all ships	Asiti, Vemira
    Particles Efficiency Enhancer	Increases base Research Efficiency for Quantum, Phantom, Surax and Metreon costs by 20%	Eldur, Bolari, Crios, Tezera
    Research Efficiency Enhancer	+40% base Research Efficiency for Parsteel, Tritanium and Dilithium costs (including Sigma)	Framtid, Otima
    Research Speed Enhancer	+75% base Research Speed	Hrojost, Roshar
    Scrapping Speed Enhancer	Increases base Scrapping Speed up to 60%	Triss, Abilakk
    Shield Enhancer	+50% base Shield Health to all ships	Barasa
    Shield Penetration Enhancer	+40% base Shield Penetration for all ships	Lenara, Parturi
    Siege Enhancer	+50% base Weapon Damage to all ships when attacking Stations	Eldur, Crios
    Surax Particle Generator	Grants Surax Particles each day in the Territory Store (Ops 25+)	Lenara, Asiti, Qeyma, Kolava, Vemira, Tigan, Perim, Parturi, Stilhe, Ezla
     */
    DUMMY(null, null, null);

    private String name;
    private String description;
    private String imageUrl;

    Service(final String name, final String description, final String imageUrl) {
        this.name = name;
        this.description = description;
        this.imageUrl = String.format("/images/services/%s", imageUrl);
    }

    Service(final String name, final String description) {
        this(name, description, computeImageUrl(name));
    }

    private static String computeImageUrl(String name) {
        return name.toLowerCase().replace(' ', '_') + ".png";
    }
}
