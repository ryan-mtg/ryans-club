package club.ryans.parser;

import lombok.Data;

@Data
public class OfficerProcEvent extends ShipEvent {
    private static final String TYPE = "Officer Proc Event";

    private String abilityType;
    private String abilityName;
    private String abilityOwnerName;
    private double abilityValue;

    public String getType() {
        return TYPE;
    }
}
