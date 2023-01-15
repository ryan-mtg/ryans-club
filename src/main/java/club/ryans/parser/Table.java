package club.ryans.parser;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Table<RowType> {
    private List<String> headers;
    private List<RowType> rows = new ArrayList<>();

    public void addRow(final RowType row) {
        rows.add(row);
    }
}