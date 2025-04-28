package club.ryans.models.inventory;

import club.ryans.models.Building;
import club.ryans.models.Mission;
import club.ryans.models.Officer;
import club.ryans.models.Research;
import club.ryans.models.Resource;
import club.ryans.models.ShipClass;
import club.ryans.models.accounting.ResourceAmount;
import club.ryans.models.managers.ItemManagerContainer;
import club.ryans.models.player.BuildingLevel;
import club.ryans.models.player.MissionStatus;
import club.ryans.models.player.OfficerLevel;
import club.ryans.models.player.ResearchLevel;
import club.ryans.models.player.Ship;
import club.ryans.models.player.Update;
import club.ryans.models.player.User;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.function.BiConsumer;

@Slf4j
public class DataCollector {
    private static final int UPDATES_LIMIT = 100;
    // TODO: I believe (at least some of) these are fleet commanders
    private static final List<Long> BAD_OFFICER_IDS =
            Arrays.asList(2305495713L, 284701693L, 2807368027L, 3132305419L, 3020118241L);

    private final ItemManagerContainer itemManagerContainer;

    @Getter
    private final List<Update> updates = new ArrayList<>();
    private final HashMap<String, BiConsumer<JsonNode, Update>> handlers = new HashMap<>();

    private int nextUpdateId = 1;

    public DataCollector(final ItemManagerContainer itemManagerContainer) {
        this.itemManagerContainer = itemManagerContainer;
        handlers.put("resource", this::handleResource);
        handlers.put("module", this::handleBuilding);
        handlers.put("ship", this::handleShip);
        handlers.put("officer", this::handleOfficer);
        handlers.put("research", this::handleResearch);
        handlers.put("ft", this::handleForbiddenTech);
        handlers.put("mission", this::handleCompleteMission);
        handlers.put("active_mission", this::handleActiveMission);
    }

    public Update collect(final User user, final String json) {
        Update update = new Update(nextUpdateId++, user, json);
        try {
            parse(json, update);
            append(update);
        } catch (Exception e) {
            LOGGER.info("  fatal error: {}", e.getMessage());
        }
        return update;
    }

    private synchronized void append(final Update update) {
        updates.add(update);
        while (updates.size() > UPDATES_LIMIT) {
            updates.remove(0);
        }
    }

    private void parse(final String json, final Update update) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode root = mapper.readTree(json);
            for (JsonNode node : root) {
                parseObject(node, update);
            }
            update.setState(Update.State.COMPLETE);
        } catch (Exception e) {
            LOGGER.info("  >> error: {}", e.getMessage());
            update.setError(e.getMessage());
        }
    }

    private void parseObject(final JsonNode node, final Update update) {
        final String type = node.get("type").asText();

        if (handlers.containsKey(type)) {
            handlers.get(type).accept(node, update);
            update.addItemCount(type);
            return;
        }

        switch (type) {
            case "ft":
            case "trait":
                update.addItemCount(type);
                return;
        }
        throw new RuntimeException("Unknown type: " + type);
    }

    private void handleResource(final JsonNode node, final Update update) {
        final long resourceId = node.get("rid").asLong();
        Resource resource = itemManagerContainer.getResourceFromStfcSpaceId(resourceId);
        if (resource == null) {
            // TODO: figure out why some resources are missing.
            return;
            //throw new RuntimeException("Unknown resource id: " + resourceId);
        }

        ResourceAmount resourceAmount = new ResourceAmount(resource, node.get("amount").asLong());
        update.addResource(resourceAmount);
    }

    private void handleBuilding(final JsonNode node, final Update update) {
        final long buildingId = node.get("bid").asLong();
        Building building = itemManagerContainer.getBuilding(buildingId);
        if (building == null) {
            if (buildingId == 76 || buildingId >= 1000) {
                return;
            }
            throw new RuntimeException("Unknown building id: " + buildingId);
        }

        BuildingLevel buildingLevel = new BuildingLevel(building, node.get("level").asInt());
        update.addBuilding(buildingLevel);
    }

    private void handleShip(final JsonNode node, final Update update) {
        final long shipId = node.get("psid").asLong();
        final long shipClassId = node.get("hull_id").asLong();
        ShipClass shipClass = itemManagerContainer.getShipClassFromStfcSpaceId(shipClassId);
        if (shipClass == null) {
            throw new RuntimeException("Unknown ship class id: " + shipClassId);
        }

        Ship ship = new Ship();
        ship.setId(shipId);
        ship.setShipClass(shipClass);
        ship.setTier(node.get("tier").asInt());
        ship.setLevel(node.get("level").asInt());
        update.addShip(ship);
    }

    private void handleOfficer(final JsonNode node, final Update update) {
        final long officerId = node.get("oid").asLong();
        Officer officer = itemManagerContainer.getOfficerFromStfcSpaceId(officerId);
        if (officer == null) {
            if (BAD_OFFICER_IDS.contains(officerId)) {
                return;
            }
            throw new RuntimeException("Unknown officer id: " + officerId);
        }

        OfficerLevel officerLevel = new OfficerLevel();
        officerLevel.setOfficer(officer);
        officerLevel.setRank(node.get("rank").asInt());
        officerLevel.setLevel(node.get("level").asInt());
        update.addOfficer(officerLevel);
    }

    private void handleResearch(final JsonNode node, final Update update) {
        final long researchId = node.get("rid").asLong();
        Research research = itemManagerContainer.getResearchFromStfcSpaceId(researchId);
        if (research == null) {
            // TODO: figure out why some researches are missing.
            return;
            //throw new RuntimeException("Unknown research id: " + researchId);
        }

        ResearchLevel researchLevel = new ResearchLevel(research, node.get("level").asInt());
        update.addResearch(researchLevel);
    }

    private void handleForbiddenTech(final JsonNode node, final Update update) {
        /*
        final long researchId = node.get("fid").asLong();
        Research research = researchManager.getResearch(researchId);
        if (research == null) {
            throw new RuntimeException("Unknown research id: " + researchId);
        }

        ResearchLevel researchLevel = new ResearchLevel();
        researchLevel.setResearch(research);
        researchLevel.setLevel(node.get("level").asInt());
        update.addResearch(researchLevel);
         */
    }

    private void handleCompleteMission(final JsonNode node, final Update update) {
        handleMission(node, update, MissionStatus.Status.COMPLETE);
    }

    private void handleActiveMission(final JsonNode node, final Update update) {
        handleMission(node, update, MissionStatus.Status.ACTIVE);
    }

    private void handleMission(final JsonNode node, final Update update, final MissionStatus.Status status) {
        final long missionId = node.get("mid").asLong();
        Mission mission = itemManagerContainer.getMission(missionId);
        if (mission == null) {
            // TODO: figure out why some missions are missing.
            return;
        }

        MissionStatus missionStatus = new MissionStatus(mission, status);
        update.addMission(missionStatus);
    }
}