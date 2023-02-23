package club.ryans.models.generators;

import club.ryans.models.Building;
import club.ryans.stfcspace.json.Field;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

@Slf4j
public class Utility {
    public static void ignoreField(final Object object, final String text) {}

    public static <Obj> void applyFields(final List<Field> fields, final Function<Long, Obj> lookup,
            final Map<String, BiConsumer<Obj, String>> fieldMap) {

        for (Field field : fields) {
            long id = Long.parseLong(field.getId());
            if (id < 0) {
                continue;
            }

            Obj object = lookup.apply(id);
            if (object == null) {
                if (field.getKey().equals("name")) {
                    LOGGER.info("failed to find object with id: {}", field.getText());
                }
                continue;
            }

            if (fieldMap.containsKey(field.getKey())) {
                fieldMap.get(field.getKey()).accept(object, field.getText());
            } else {
                LOGGER.info("Unknown field: {}", field.getKey());
            }
        }
    }
}
