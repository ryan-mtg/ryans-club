package club.ryans.stfcspace;

import club.ryans.stfcspace.json.*;
import club.ryans.stfcspace.json.System;
import club.ryans.stfcspace.json.ships.ShipClassDetails;

import java.util.List;

public interface StfcSpaceClient {
    List<Field> buildings();

    List<Building> building();

    BuildingDetails building(long id);

    List<Field> researches();

    List<Research> research();

    ResearchDetails research(long id);

    List<Field> officers();

    List<Field> officerNames();

    List<Field> officerBuffs();

    List<Field> officerFlavorText();

    List<Officer> officer();

    List<Field> factions();

    List<Field> resources();

    List<Resource> resource();

    List<Field> missions();

    List<System> system();

    List<Field> systems();

    List<Mission> mission();

    List<Field> ships();

    List<Ship> ship();

    ShipClassDetails ship(long id);
}