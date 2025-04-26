package club.ryans.views.inventory;

import club.ryans.models.Resource;
import club.ryans.models.player.PlayerItems;
import club.ryans.models.accounting.ResourceAmount;
import club.ryans.utility.parser.FieldParser;
import club.ryans.utility.parser.ResourceParser;
import club.ryans.utility.parser.StructParser;
import club.ryans.views.inventory.annotators.InventoryAnnotator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
@Slf4j
public class InventoryGrouper {
    private static final String INVENTORY_GROUPS_DESCRIPTION_FILE = "data/inventory/groups.txt";

    private final ResourceParser resourceParser;

    private final InventoryDescriptor inventoryDescriptor;

    private final Map<Long, List<InventoryAnnotator>> annotatorMap = new HashMap<>();

    public InventoryGrouper(final ResourceParser resourceParser, final Collection<InventoryAnnotator> annotators) {
        this.resourceParser = resourceParser;
        StructParser<InventoryDescriptor> inventoryParser = createInventoryParser();
        inventoryDescriptor = inventoryParser.parseSingle(INVENTORY_GROUPS_DESCRIPTION_FILE);
        inventoryDescriptor.getGroups();
        annotators.forEach(annotator -> annotator.getApplicableResourceIds().forEach(resourceId -> {
            if (!annotatorMap.containsKey(resourceId)) {
                annotatorMap.put(resourceId, new ArrayList<>());
            }
            annotatorMap.get(resourceId).add(annotator);
        }));
    }

    public Inventory group(final PlayerItems playerItems) {
        Inventory inventory = new Inventory();
        Set<Long> added = addGroups(inventory, playerItems.getResources());
        addMiscellaneousGroup(inventory, playerItems.getResources(), added);
        setMainGroup(inventory);
        applyAnnotators(inventory, playerItems);
        return inventory;
    }

    private StructParser<InventoryDescriptor> createInventoryParser() {
        StructParser<InventoryGroupDescriptor> groupParser = createInventoryGroupParser();

        return StructParser.createStructParser(InventoryDescriptor.class, "inventory",
                FieldParser.structArrayField("groups", groupParser, InventoryDescriptor::setGroups));
    }

    private StructParser<InventoryGroupDescriptor> createInventoryGroupParser() {
        StructParser<InventorySubgroupDescriptor> subgroupParser = createInventorySubgroupParser();

        return StructParser.createStructParser(InventoryGroupDescriptor.class, "group",
                FieldParser.stringField("name", InventoryGroupDescriptor::setName),
                FieldParser.stringField("id", InventoryGroupDescriptor::setId),
                FieldParser.booleanField("default", InventoryGroupDescriptor::setMain),
                FieldParser.structArrayField("subgroups", subgroupParser, InventoryGroupDescriptor::setSubgroups));
    }

    private StructParser<InventorySubgroupDescriptor> createInventorySubgroupParser() {
        return StructParser.createStructParser(InventorySubgroupDescriptor.class, "subgroup",
                FieldParser.stringField("name", InventorySubgroupDescriptor::setName),
                FieldParser.stringField("description", InventorySubgroupDescriptor::setDescription),
                FieldParser.resourceArrayField("resources", resourceParser, InventorySubgroupDescriptor::setResources));
    }

    private Set<Long> addGroups(final Inventory inventory, final Map<Long, ResourceAmount> resources) {
        Set<Long> added = new HashSet<>();
        List<InventoryGroup> groups = new ArrayList<>();

        for (InventoryGroupDescriptor groupDescriptor : inventoryDescriptor.getGroups()) {
            InventoryGroup group = createInventoryGroup(groupDescriptor, resources, added);
            if (group != null) {
                groups.add(group);
            }
        }

        inventory.setGroups(groups);
        return added;
    }

    private InventoryGroup createInventoryGroup(final InventoryGroupDescriptor groupDescriptor,
        final Map<Long, ResourceAmount> resources, final Set<Long> added) {

        List<InventorySubgroup> subgroups = new ArrayList<>();

        for (InventorySubgroupDescriptor subgroupDescriptor : groupDescriptor.getSubgroups()) {
            InventorySubgroup subgroup = createInventorySubgroup(subgroupDescriptor, resources, added);
            if (subgroup != null) {
                subgroups.add(subgroup);
            }
        }

        if (subgroups.isEmpty()) {
            return null;
        }

        return new InventoryGroup(groupDescriptor.getName(), groupDescriptor.getId(), groupDescriptor.isMain(),
                subgroups);
    }

    private InventorySubgroup createInventorySubgroup(final InventorySubgroupDescriptor subgroupDescriptor,
            final Map<Long, ResourceAmount> resources, final Set<Long> added) {

        List<InventoryItem> subgroupItems = new ArrayList<>();

        for (Resource resource : subgroupDescriptor.getResources()) {
            final long resourceId = resource.getId();
            if (resources.containsKey(resourceId)) {
                InventoryItem item = new InventoryItem(resources.get(resourceId));
                subgroupItems.add(item);
                added.add(resourceId);
            }
        }

        if (subgroupItems.isEmpty()) {
            return null;
        }

        return new InventorySubgroup(subgroupDescriptor.getName(), subgroupDescriptor.getDescription(), subgroupItems);
    }

    private void addMiscellaneousGroup(final Inventory inventory, final Map<Long, ResourceAmount> resources,
            final Set<Long> added) {

        List<InventoryItem> groupItems = new ArrayList<>();

        for (Map.Entry<Long, ResourceAmount> entry :  resources.entrySet()) {
            if (!added.contains(entry.getKey())) {
                groupItems.add(new InventoryItem(entry.getValue()));
            }
        }

        if (groupItems.isEmpty()) {
            return;
        }

        InventorySubgroup subgroup = new InventorySubgroup("Unsorted", null, groupItems);
        InventoryGroup group = new InventoryGroup("Miscellaneous", "miscellaneous", false, Arrays.asList(subgroup));
        inventory.getGroups().add(group);
    }

    private void setMainGroup(final Inventory inventory) {
        boolean hasDefaultGroup = false;
        for (InventoryGroup group : inventory.getGroups()) {
            if (group.isMain()) {
                inventory.setDefaultGroupId(group.getId());
                hasDefaultGroup = true;
                break;
            }
        }

        if (!hasDefaultGroup && !inventory.getGroups().isEmpty()) {
            InventoryGroup firstGroup = inventory.getGroups().get(0);
            firstGroup.setMain(true);
            inventory.setDefaultGroupId(firstGroup.getId());
        }

        for (InventoryGroup group : inventory.getGroups()) {
            group.setMain(inventory.getDefaultGroupId().equals(group.getId()));
        }
    }

    private void applyAnnotators(final Inventory inventory, final PlayerItems playerItems) {
        List<InventoryItem> items = new ArrayList<>();
        populateItemList(inventory, items);
        items.forEach(item -> {
            if (annotatorMap.containsKey(item.getResourceId())) {
                annotatorMap.get(item.getResourceId()).forEach(annotator -> {
                    List<String> annotations = annotator.getAnnotations(item, playerItems);
                    for (String annotation : annotations) {
                        item.addAnnotation(annotation);
                    }
                });
            }
        });
    }

    void populateItemList(final Inventory inventory, final List<InventoryItem> items) {
        inventory.getGroups().forEach(group -> populateItemList(group, items));
    }

    void populateItemList(final InventoryGroup group, final List<InventoryItem> items) {
        group.getSubgroups().forEach(subgroup -> populateItemList(subgroup, items));
    }

    void populateItemList(final InventorySubgroup subgroup, final List<InventoryItem> items) {
        items.addAll(subgroup.getItems());
    }
}