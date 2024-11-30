package club.ryans.models.generators;

import club.ryans.stfcspace.json.Field;
import lombok.extern.slf4j.Slf4j;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;

@Slf4j
public class Utility {
    public static void ignoreField(final Object object, final String text) {}

    public static <Obj> void applyFields(final List<Field> fields, final Function<Long, Obj> lookup,
            final Map<String, BiConsumer<Obj, String>> fieldMap) {

        Set<String> unknownIdKeys = new HashSet<>();
        Set<String> unknownFields = new HashSet<>();

        for (Field field : fields) {
            if (field.getId() == null) {
                continue;
            }

            String key = field.getKey();
            if (!fieldMap.containsKey(key)) {
                unknownFields.add(key);
                continue;
            }

            long id = Long.parseLong(field.getId());
            if (id < 0) {
                continue;
            }

            Obj object = lookup.apply(id);
            if (object == null) {
                unknownIdKeys.add(key);
                if (key.equals("name") || key.endsWith("name")) {
                    LOGGER.info("failed to find object with id: {}", field.getText());
                }
                continue;
            }

            if (fieldMap.containsKey(key)) {
                fieldMap.get(key).accept(object, field.getText());
            } else {
                unknownFields.add(key);
            }
        }

        if (!unknownIdKeys.isEmpty()) {
            LOGGER.info("Unknown id keys: {}", String.join(", ", unknownIdKeys));
        }

        if (!unknownFields.isEmpty()) {
            LOGGER.info("Unknown fields: {}", String.join(", ", unknownFields));
        }
    }
}
