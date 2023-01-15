package club.ryans.parser;

import lombok.Data;

@Data
public abstract class BattleEvent {
    private int round;
    private int index;

    abstract String getType();
}