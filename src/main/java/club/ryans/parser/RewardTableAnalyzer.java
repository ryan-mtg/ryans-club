package club.ryans.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class RewardTableAnalyzer implements TableAnalyzer {
    private static final String NAME = "Reward Name";
    private static final String AMOUNT = "Count";

    private static final Map<String, Function<String, Object>> CONVERTERS =
            new HashMap<String, Function<String, Object>>(){{
                put(NAME, Converters::convertString);
                put(AMOUNT, Converters::convertInteger);
            }};

    public Map<String, Function<String, Object>> getConverters() {
        return CONVERTERS;
    }

    public void analyze(final Log log, final ParseTable parseTable) {
        List<String> headers = parseTable.getHeaders();

        List<Reward> rewards = new ArrayList<>();
        for (List<String> parsedRow : parseTable.getRows()) {
            Map<String, Object> convertedRow = convertRow(headers, parsedRow);
            rewards.add(createReward(convertedRow));
        }

        log.setRewards(rewards);
    }

    private Reward createReward(final Map<String, Object> row) {
        Reward reward = new Reward();
        reward.setName(getValue(NAME, row));
        reward.setAmount(getValue(AMOUNT, row));
        return reward;
    }
}
