package club.ryans.data.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Entity
@Table(name = "ship")
@IdClass(ShipRow.ShipId.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShipRow {
    @Id
    @Column(name = "account_id")
    private int accountId;

    @Id
    @Column(name = "ship_id")
    private long shipId;

    @Id
    @Column(name = "ship_class_id")
    private long shipClassId;

    @NotNull
    private int tier;

    @NotNull
    private int level;

    @Data @NoArgsConstructor
    public static class ShipId implements Serializable {
        private int accountId;
        private long shipId;
    }
}