package club.ryans.models;

import lombok.Data;

@Data
public class Reward {
    private long resourceId;
    private int type;
    private int amount;
}
