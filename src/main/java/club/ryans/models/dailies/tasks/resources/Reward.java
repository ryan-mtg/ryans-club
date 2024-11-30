package club.ryans.models.dailies.tasks.resources;

import club.ryans.charts.models.RowValue;
import club.ryans.models.Resource;
import club.ryans.views.Utility;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.With;

import java.util.List;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class Reward {
    private boolean unknown = false;
    private Resource resource;

    private int rolls;
    @With
    private int chests;
    private float chance;
    private List<Float> probabilities;
    private RowValue value;

    public boolean hasExpectedValue() {
        return probabilities != null || (chance < 1 && value.isSingleValued());
    }

    public String getDisplay() {
        if (unknown) {
            return "?";
        }

        if (hasExpectedValue()) {
            return String.format("EV %s", Utility.scoplify(expectedValue()));
        }

        if (value.isMultivalued()) {
            StringBuilder result = new StringBuilder();
            boolean first = true;
            for (long v : value.getValues()) {
                if (first) {
                    first = false;
                } else {
                    result.append('/');
                }
                result.append(Utility.scoplify(v * rolls * chests));
            }
            return result.toString();
        }

        return Utility.scoplify(value.getLong() * rolls * chests);
    }

    public boolean isEmpty() {
        if (isUnknown()) {
            return false;
        }

        if (value.isSingleValued()) {
            return value.getLong() == 0;
        }

        // Technically, some of the other ways could make it empty (0 rolls, 0% chance),
        // but I don't think that happens in practice.
        return false;
    }

    public long expectedValue() {
        if (value.isSingleValued()) {
            return (long) Math.floor(chance * value.getLong() * rolls * chests);
        }

        if (probabilities == null) {
            return -1;
        }

        if (!value.isMultivalued() || value.getValues().size() != probabilities.size()) {
            return -1;
        }

        double total = 0;
        for (int i = 0; i < probabilities.size(); i++) {
            total += probabilities.get(i)/100 * value.getValues().get(i);
        }
        return (long) Math.floor(chance * total * rolls * chests);
    }
}