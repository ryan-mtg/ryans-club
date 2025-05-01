package club.ryans.charts.models;

import club.ryans.models.items.Resource;
import lombok.Getter;
import lombok.Setter;

import java.time.Duration;
import java.util.Collections;
import java.util.List;

@Getter @Setter
public class ColumnGroup {
    private String title;
    private String id;
    private Resource resource;
    private String color;
    private String background;
    private Duration cooldown;

    private List<Column> columns;

    public static ColumnGroup create(final Column column) {
        ColumnGroup columnGroup = new ColumnGroup();
        columnGroup.setColumns(Collections.singletonList(column));
        return columnGroup;
    }

    public int getNumberOfColumns() {
        return columns.size();
    }
}
